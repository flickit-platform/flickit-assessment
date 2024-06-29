package org.flickit.assessment.core.adapter.in.rest.evidenceattachment;

import org.flickit.assessment.core.application.domain.EvidenceAttachment;

import java.util.List;

public record GetEvidenceAttachmentsResponseDto(List<EvidenceAttachment> attachments) {
}
