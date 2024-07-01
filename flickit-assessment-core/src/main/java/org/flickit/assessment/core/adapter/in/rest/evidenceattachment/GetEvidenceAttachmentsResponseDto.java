package org.flickit.assessment.core.adapter.in.rest.evidenceattachment;

import org.flickit.assessment.core.application.port.in.evidenceattachment.GetEvidenceAttachmentsUseCase.EvidenceAttachmentsItem;

import java.util.List;

public record GetEvidenceAttachmentsResponseDto(List<EvidenceAttachmentsItem> attachments) {
}
