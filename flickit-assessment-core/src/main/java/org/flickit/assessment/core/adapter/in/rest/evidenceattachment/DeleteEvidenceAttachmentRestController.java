package org.flickit.assessment.core.adapter.in.rest.evidenceattachment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.evidenceattachment.DeleteEvidenceAttachmentUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteEvidenceAttachmentRestController {

    private final DeleteEvidenceAttachmentUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("evidences/{evidenceId}/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteEvidenceAttachment(@PathVariable("evidenceId") UUID evidenceId,
                                                         @PathVariable("attachmentId") UUID attachmentId) {
        UUID currentUserId = userContext.getUser().id();
        useCase.deleteEvidenceAttachment(toParam(evidenceId, attachmentId, currentUserId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private DeleteEvidenceAttachmentUseCase.Param toParam(UUID evidenceId, UUID attachmentId, UUID currentUserId) {
        return new DeleteEvidenceAttachmentUseCase.Param(evidenceId, attachmentId, currentUserId);
    }
}
