package org.flickit.assessment.core.adapter.in.rest.evidenceattachment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.evidenceattachment.AddEvidenceAttachmentUseCase;
import org.flickit.assessment.core.application.port.in.evidenceattachment.AddEvidenceAttachmentUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AddEvidenceAttachmentRestController {

    private final AddEvidenceAttachmentUseCase useCase;
    private final UserContext userContext;

    @PostMapping("evidences/{id}/attachments")
    public ResponseEntity<AddEvidenceAttachmentResponseDto> addEvidenceAttachment(@PathVariable("id") UUID id,
                                                                                  @RequestParam("attachment") MultipartFile attachment,
                                                                                  @RequestBody(required = false) AddEvidenceAttachmentRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        var response = toResponseDto(useCase.addAttachment(toParam(id, attachment, requestDto, currentUserId)));

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private Param toParam(UUID evidenceId, MultipartFile attachment, AddEvidenceAttachmentRequestDto requestDto, UUID currentUserId) {
        return new Param(evidenceId, attachment, requestDto.description(), currentUserId);
    }

    private AddEvidenceAttachmentResponseDto toResponseDto(Result result) {
        return new AddEvidenceAttachmentResponseDto(result.attachmentId(), result.attachmentLink());
    }
}
