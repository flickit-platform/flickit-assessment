package org.flickit.assessment.core.adapter.in.rest.evidenceattachment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.evidenceattachment.GetEvidenceAttachmentsUseCase;
import org.flickit.assessment.core.application.port.in.evidenceattachment.GetEvidenceAttachmentsUseCase.EvidenceAttachmentsItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetEvidenceAttachmentsRestController {

    private final GetEvidenceAttachmentsUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/evidences/{id}/attachments")
    public ResponseEntity<GetEvidenceAttachmentsResponseDto> getEvidenceAttachments(@PathVariable UUID id) {
        var currentUserId = userContext.getUser().id();
        var evidenceAttachments = useCase.getEvidenceAttachments(toParam(id, currentUserId));
        return new ResponseEntity<>(toResponseDto(evidenceAttachments), HttpStatus.OK);
    }

    private GetEvidenceAttachmentsUseCase.Param toParam(UUID id, UUID currentUserId) {
        return new GetEvidenceAttachmentsUseCase.Param(id, currentUserId);
    }

    private GetEvidenceAttachmentsResponseDto toResponseDto(List<EvidenceAttachmentsItem> evidenceAttachments) {
        return new GetEvidenceAttachmentsResponseDto(evidenceAttachments);
    }
}
