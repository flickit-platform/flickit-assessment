package org.flickit.assessment.core.adapter.in.rest.evidenceattachment;

import org.flickit.assessment.core.application.port.in.evidenceattachment.GetEvidenceAttachmentsUseCase.Attachment;

import java.util.List;

public record GetEvidenceAttachmentsResponseDto(List<Attachment> attachments) {
}
