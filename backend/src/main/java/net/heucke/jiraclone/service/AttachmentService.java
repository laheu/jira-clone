package net.heucke.jiraclone.service;

import net.heucke.jiraclone.config.JiraProperties;
import net.heucke.jiraclone.repo.AttachmentRepository;
import net.heucke.jiraclone.repo.Rows.AttachmentRow;
import net.heucke.jiraclone.web.NotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Locates attachment binaries in Jira's filesystem attachment store. Jira
 * stores the metadata in the {@code fileattachment} table but the files
 * themselves under {@code <jira-home>/data/attachments}, named by their id:
 * <ul>
 *   <li>Jira 8.1+ layout: {@code <PROJECT>/<bucket>/<PROJECT-NUM>/<id>}
 *       (bucket = issue number rounded up to the next 10000)</li>
 *   <li>legacy layout: {@code <PROJECT>/<PROJECT-NUM>/<id>}</li>
 * </ul>
 */
@Service
public class AttachmentService {

    public record AttachmentFile(AttachmentRow meta, Path path) {
    }

    private final AttachmentRepository attachmentRepository;
    private final JiraProperties jiraProperties;

    public AttachmentService(AttachmentRepository attachmentRepository, JiraProperties jiraProperties) {
        this.attachmentRepository = attachmentRepository;
        this.jiraProperties = jiraProperties;
    }

    public AttachmentFile resolve(long attachmentId) {
        AttachmentRow meta = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new NotFoundException("Anhang nicht gefunden: " + attachmentId));
        if (!jiraProperties.attachmentsEnabled()) {
            throw new NotFoundException(
                    "Anhang-Speicher ist nicht konfiguriert (app.jira.attachments-dir bzw. JIRA_ATTACHMENTS_DIR)");
        }
        Path baseDir = Path.of(jiraProperties.attachmentsDir());
        long bucket = ((meta.issueNum() - 1) / 10_000 + 1) * 10_000;
        String fileName = String.valueOf(meta.id());
        List<Path> candidates = List.of(
                baseDir.resolve(meta.projectKey()).resolve(String.valueOf(bucket))
                        .resolve(meta.issueKey()).resolve(fileName),
                baseDir.resolve(meta.projectKey()).resolve(meta.issueKey()).resolve(fileName));
        return candidates.stream()
                .filter(Files::isRegularFile)
                .findFirst()
                .map(path -> new AttachmentFile(meta, path))
                .orElseThrow(() -> new NotFoundException(
                        "Datei zu Anhang " + attachmentId + " (" + meta.filename() + ") nicht im Anhang-Speicher gefunden"));
    }
}
