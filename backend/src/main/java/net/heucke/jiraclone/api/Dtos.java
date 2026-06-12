package net.heucke.jiraclone.api;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * JSON payloads of the REST API.
 */
public final class Dtos {

    private Dtos() {
    }

    public record UserRef(String key, String displayName, String email) {
    }

    public record StatusRef(String id, String name, String category) {

        /** Maps Jira's statuscategory id (2=new, 3=done, 4=indeterminate) to a stable key. */
        public static StatusRef of(String id, String name, Integer categoryId) {
            String category = switch (categoryId == null ? 0 : categoryId) {
                case 2 -> "TO_DO";
                case 3 -> "DONE";
                case 4 -> "IN_PROGRESS";
                default -> "UNDEFINED";
            };
            return new StatusRef(id, name, category);
        }
    }

    public record TypeRef(String id, String name) {
    }

    public record PriorityRef(String id, String name, String color) {
    }

    public record ProjectDto(long id, String key, String name, String description,
                             UserRef lead, long issueCount) {
    }

    public record IssueSummaryDto(String key, String summary, TypeRef type, StatusRef status,
                                  PriorityRef priority, UserRef assignee,
                                  Instant created, Instant updated, LocalDate dueDate) {
    }

    public record CommentDto(long id, UserRef author, String body, Instant created, Instant updated) {
    }

    public record TransitionDto(int id, String name, StatusRef targetStatus) {
    }

    public record AttachmentDto(long id, String filename, String mimeType, long size,
                                UserRef author, Instant created) {
    }

    public record IssueDetailDto(String key, String summary, String description,
                                 TypeRef type, StatusRef status, PriorityRef priority,
                                 String resolution, UserRef assignee, UserRef reporter,
                                 List<String> labels,
                                 Instant created, Instant updated, LocalDate dueDate, Instant resolutionDate,
                                 List<CommentDto> comments, List<TransitionDto> transitions,
                                 IssueSummaryDto parent, List<IssueSummaryDto> children,
                                 List<AttachmentDto> attachments) {
    }

    public record PageDto<T>(List<T> items, int page, int size, long total) {
    }

    public record MetaDto(List<StatusRef> statuses, List<TypeRef> types, List<PriorityRef> priorities) {
    }

    /** Statuses and types that actually occur in one project (for filter dropdowns). */
    public record ProjectMetaDto(List<StatusRef> statuses, List<TypeRef> types) {
    }
}
