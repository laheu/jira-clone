package net.heucke.jiraclone.service;

import net.heucke.jiraclone.api.Dtos.AttachmentDto;
import net.heucke.jiraclone.api.Dtos.CommentDto;
import net.heucke.jiraclone.api.Dtos.IssueDetailDto;
import net.heucke.jiraclone.api.Dtos.IssueSummaryDto;
import net.heucke.jiraclone.api.Dtos.PageDto;
import net.heucke.jiraclone.api.Dtos.PriorityRef;
import net.heucke.jiraclone.api.Dtos.StatusRef;
import net.heucke.jiraclone.api.Dtos.TypeRef;
import net.heucke.jiraclone.api.Dtos.UserRef;
import net.heucke.jiraclone.repo.AttachmentRepository;
import net.heucke.jiraclone.repo.CommentRepository;
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

    private final IssueRepository issueRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final WorkflowService workflowService;
    private final HierarchyRepository hierarchyRepository;
    private final AttachmentRepository attachmentRepository;

    public IssueService(IssueRepository issueRepository,
                        CommentRepository commentRepository,
                        UserRepository userRepository,
                        WorkflowService workflowService,
                        HierarchyRepository hierarchyRepository,
                        AttachmentRepository attachmentRepository) {
        this.issueRepository = issueRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.workflowService = workflowService;
        this.hierarchyRepository = hierarchyRepository;
        this.attachmentRepository = attachmentRepository;
    }

    public PageDto<IssueSummaryDto> search(String projectKey, String statusId, String typeId, String text,
                                           int page, int size) {
        int safeSize = Math.clamp(size, 1, 100);
        int safePage = Math.max(page, 0);
        List<IssueRow> rows = issueRepository.search(projectKey, statusId, typeId, text,
                safePage * safeSize, safeSize);
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

        Set<String> userKeys = new HashSet<>();
        userKeys.add(row.assigneeKey());
        userKeys.add(row.reporterKey());
        comments.forEach(c -> userKeys.add(c.authorKey()));
        attachments.forEach(a -> userKeys.add(a.authorKey()));
        childRows.forEach(c -> userKeys.add(c.assigneeKey()));
        if (parentRow != null) {
            userKeys.add(parentRow.assigneeKey());
        }
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
                        .toList());
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
