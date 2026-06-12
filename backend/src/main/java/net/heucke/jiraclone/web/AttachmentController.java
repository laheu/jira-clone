package net.heucke.jiraclone.web;

import net.heucke.jiraclone.service.AttachmentService;
import net.heucke.jiraclone.service.AttachmentService.AttachmentFile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @GetMapping("/api/attachments/{id}")
    public ResponseEntity<Resource> download(@PathVariable long id) {
        AttachmentFile file = attachmentService.resolve(id);
        return ResponseEntity.ok()
                .contentType(parseMediaType(file.meta().mimeType()))
                .headers(headers -> headers.setContentDisposition(
                        ContentDisposition.inline()
                                .filename(file.meta().filename(), StandardCharsets.UTF_8)
                                .build()))
                .body(new FileSystemResource(file.path()));
    }

    private static MediaType parseMediaType(String mimeType) {
        try {
            return MediaType.parseMediaType(mimeType);
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
