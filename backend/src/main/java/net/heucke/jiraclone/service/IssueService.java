package net.heucke.jiraclone.service;

import net.heucke.jiraclone.api.Dtos.AttachmentDto;
import net.heucke.jiraclone.api.Dtos.CommentDto;
import net.heucke.jiraclone.api.Dtos.CustomFieldDto;
import net.heucke.jiraclone.api.Dtos.IssueDetailDto;
import net.heucke.jiraclone.api.Dtos.IssueSummaryDto;
import net.heucke.jiraclone.api.Dtos.PageDto;
import net.heucke.jiraclone.api.Dtos.PriorityRef;
import net.heucke.jiraclone.api.Dtos.StatusRef;
import net.heucke.jiraclone.api.Dtos.TypeRef;
import net.heucke.jiraclone.api.Dtos.UserRef;
import net.heucke.jiraclone.repo.AttachmentRepository;
import net.heucke.jiraclone.repo.CommentRepository;
import net.heucke.jiraclone.repo.CustomFieldRepository;
import net.heucke.jiraclone.repo.CustomFieldRepository.CustomFieldValueRow;
import net.heucke.jiraclone.repo.HierarchyRepository;
import net.heucke.jiraclone.repo.IssueRepository;
import net.heucke.jiraclone.repo.Rows.AttachmentRow;
import net.heucke.jiraclone.repo.Rows.CommentRow;
import net.heucke.jiraclone.repo.Rows.IssueRow;
import net.heucke.jiraclone.repo.Rows.UserRow;
import net.heucke.jiraclone.repo.UserRepository;
import net.heucke.jiraclone.web.BadRequestException;
import net.heucke.jiraclone.web.NotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IssueService {

    private static final Pattern ISSUE_KEY = Pattern.compile("([A-Za-z][A-Za-z0-9_]*)-(\\d+)");

    /** Internal/system custom fields that must not show up as business fields. */
    private static final Set<String> HIDDEN_CUSTOM_FIELD_TYPES = Set.of(
            "com.pyxis.greenhopper.jira:gh-epic-link",
            "com.pyxis.greenhopper.jira:gh-lexo-rank",
            "com.pyxis.greenhopper.jira:gh-sprint",
            "com.pyxis.greenhopper.jira:gh-global-rank");

    private final IssueRepository issueRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final WorkflowService workflowService;
    private final HierarchyRepository hierarchyRepository;
    private final AttachmentRepository attachmentRepository;
    private final CustomFieldRepository customFieldRepository;

    public IssueService(IssueRepository issueRepository,
                        CommentRepository commentRepository,
                        UserRepository userRepository,
                        WorkflowService workflowService,
                        HierarchyRepository hierarchyRepository,
                        AttachmentRepository attachmentRepository,
                        CustomFieldRepository customFieldRepository) {
        this.issueRepository = issueRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.workflowService = workflowService;
        this.hierarchyRepository = hierarchyRepository;
        this.attachmentRepository = attachmentRepository;
        this.customFieldRepository = customFieldRepository;
    }

    public PageDto<IssueSummaryDto> search(String projectKey, String statusId, String typeId, String text,
                                           String sort, boolean descending, int page, int size) {
        int safeSize = Math.clamp(size, 1, 100);
        int safePage = Math.max(page, 0);
        List<IssueRow> rows = issueRepository.search(projectKey, statusId, typeId, text,
                sort, descending, safePage * safeSize, safeSize);
        long total = issueRepository.count(projectKey, statusId, typeId, text);

        Set<String> userKeys = new HashSet<>();
        rows.forEach(row -> userKeys.add(row.assigneeKey()));
        Map<String, UserRow> users = userRepository.resolve(userKeys);

        List<IssueSummaryDto> items = rows.stream()
                .map(row -> toSummary(row, users))
                .toList();
        return new PageDto<>(items, safePage, safeSize, total);
    }

    public IssueDetailDto get(String issueKey) {
        Matcher matcher = ISSUE_KEY.matcher(issueKey.trim());
        if (!matcher.matches()) {
            throw new BadRequestException("Ungültiger Vorgangs-Schlüssel: " + issueKey);
        }
        IssueRow row = issueRepository.findByKey(matcher.group(1), Long.parseLong(matcher.group(2)))
                .orElseThrow(() -> new NotFoundException("Vorgang nicht gefunden: " + issueKey));

        List<CommentRow> comments = commentRepository.findByIssueId(row.id());
        List<String> labels = issueRepository.findLabels(row.id());
        List<AttachmentRow> attachments = attachmentRepository.findByIssueId(row.id());

        IssueRow parentRow = hierarchyRepository.findParentIds(row.id()).stream()
                .findFirst()
                .flatMap(id -> issueRepository.findByIds(List.of(id)).stream().findFirst())
                .orElse(null);
        List<IssueRow> childRows = issueRepository.findByIds(hierarchyRepository.findChildIds(row.id()));

        // Only fields that are actually configured for this project (plus globally scoped ones).
        Set<Long> projectFieldIds = customFieldRepository.findFieldIdsForProject(row.projectId());
        List<CustomFieldValueRow> customFieldRows = customFieldRepository.findValues(row.id()).stream()
                .filter(cf -> !HIDDEN_CUSTOM_FIELD_TYPES.contains(cf.typeKey())
                        && (cf.typeKey() == null || !cf.typeKey().startsWith("com.atlassian.jpo"))
                        && projectFieldIds.contains(cf.fieldId()))
                .toList();

        Set<String> userKeys = new HashSet<>();
        userKeys.add(row.assigneeKey());
        userKeys.add(row.reporterKey());
        comments.forEach(c -> userKeys.add(c.authorKey()));
        attachments.forEach(a -> userKeys.add(a.authorKey()));
        childRows.forEach(c -> userKeys.add(c.assigneeKey()));
        if (parentRow != null) {
            userKeys.add(parentRow.assigneeKey());
        }
        customFieldRows.stream()
                .filter(cf -> cf.typeKey() != null && cf.typeKey().contains("userpicker"))
                .forEach(cf -> userKeys.add(cf.stringValue()));
        Map<String, UserRow> users = userRepository.resolve(userKeys);

        return new IssueDetailDto(
                row.issueKey(),
                row.summary(),
                row.description(),
                new TypeRef(row.typeId(), row.typeName()),
                StatusRef.of(row.statusId(), row.statusName(), row.statusCategory()),
                new PriorityRef(row.priorityId(), row.priorityName(), row.priorityColor()),
                row.resolutionName(),
                toUserRef(row.assigneeKey(), users),
                toUserRef(row.reporterKey(), users),
                labels,
                toInstant(row.created()),
                toInstant(row.updated()),
                toLocalDate(row.dueDate()),
                toInstant(row.resolutionDate()),
                comments.stream()
                        .map(c -> new CommentDto(c.id(), toUserRef(c.authorKey(), users),
                                c.body(), toInstant(c.created()), toInstant(c.updated())))
                        .toList(),
                workflowService.transitionsFor(row),
                parentRow == null ? null : toSummary(parentRow, users),
                childRows.stream().map(c -> toSummary(c, users)).toList(),
                attachments.stream()
                        .map(a -> new AttachmentDto(a.id(), a.filename(), a.mimeType(), a.fileSize(),
                                toUserRef(a.authorKey(), users), toInstant(a.created())))
                        .toList(),
                toCustomFields(customFieldRows, users));
    }

    /**
     * Groups raw custom field values by field (multi-value fields produce
     * several rows) and renders each value: select options are looked up in
     * customfieldoption, user keys become display names, dates and numbers
     * are formatted.
     */
    private List<CustomFieldDto> toCustomFields(List<CustomFieldValueRow> rows, Map<String, UserRow> users) {
        Set<Long> selectFieldIds = rows.stream()
                .filter(cf -> isSelectType(cf.typeKey()))
                .map(CustomFieldValueRow::fieldId)
                .collect(java.util.stream.Collectors.toSet());
        Map<String, String> options = customFieldRepository.findOptions(selectFieldIds);

        Map<Long, CustomFieldDto> byField = new java.util.LinkedHashMap<>();
        for (CustomFieldValueRow cf : rows) {
            String value = renderValue(cf, options, users);
            if (value == null || value.isBlank()) {
                continue;
            }
            boolean multiline = isMultilineType(cf.typeKey());
            byField.merge(cf.fieldId(), new CustomFieldDto(cf.fieldId(), cf.name(), value, multiline),
                    (a, b) -> new CustomFieldDto(a.id(), a.name(), a.value() + ", " + b.value(), a.multiline()));
        }
        // Single-line fields first (sidebar), multiline sections afterwards.
        return byField.values().stream()
                .sorted(java.util.Comparator.comparing(CustomFieldDto::multiline))
                .toList();
    }

    private static boolean isSelectType(String typeKey) {
        return typeKey != null
                && (typeKey.contains("select") || typeKey.contains("radiobuttons") || typeKey.contains("checkboxes"));
    }

    private static boolean isMultilineType(String typeKey) {
        return typeKey != null && typeKey.contains("textarea");
    }

    private static String renderValue(CustomFieldValueRow cf, Map<String, String> options,
                                      Map<String, UserRow> users) {
        if (cf.dateValue() != null) {
            var dateTime = cf.dateValue().toLocalDateTime();
            return dateTime.toLocalTime().equals(java.time.LocalTime.MIDNIGHT)
                    ? dateTime.toLocalDate().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                    : dateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        }
        if (cf.numberValue() != null) {
            return cf.numberValue().stripTrailingZeros().toPlainString();
        }
        String raw = cf.stringValue() != null ? cf.stringValue() : cf.textValue();
        if (raw == null) {
            return null;
        }
        if (isSelectType(cf.typeKey())) {
            return options.getOrDefault(raw, raw);
        }
        if (cf.typeKey() != null && cf.typeKey().contains("userpicker")) {
            UserRow user = users.get(raw);
            return user != null ? user.displayName() : raw;
        }
        return raw;
    }

    private static IssueSummaryDto toSummary(IssueRow row, Map<String, UserRow> users) {
        return new IssueSummaryDto(
                row.issueKey(),
                row.summary(),
                new TypeRef(row.typeId(), row.typeName()),
                StatusRef.of(row.statusId(), row.statusName(), row.statusCategory()),
                new PriorityRef(row.priorityId(), row.priorityName(), row.priorityColor()),
                toUserRef(row.assigneeKey(), users),
                toInstant(row.created()),
                toInstant(row.updated()),
                toLocalDate(row.dueDate()));
    }

    private static UserRef toUserRef(String userKey, Map<String, UserRow> users) {
        if (userKey == null) {
            return null;
        }
        UserRow user = users.get(userKey);
        return user != null
                ? new UserRef(user.key(), user.displayName(), user.email())
                : new UserRef(userKey, userKey, null);
    }

    private static Instant toInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }

    private static LocalDate toLocalDate(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime().toLocalDate();
    }
}
