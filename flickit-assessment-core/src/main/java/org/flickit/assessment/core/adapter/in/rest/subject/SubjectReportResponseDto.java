package org.flickit.assessment.core.adapter.in.rest.subject;

import org.flickit.assessment.core.application.domain.report.SubjectAttributeReportItem;
import org.flickit.assessment.core.application.domain.report.SubjectReportItem;
import org.flickit.assessment.core.application.domain.report.TopAttribute;

import java.util.List;

public record SubjectReportResponseDto(SubjectReportItem subject,
                                       List<TopAttribute> topStrengths,
                                       List<TopAttribute> topWeaknesses,
                                       List<SubjectAttributeReportItem> attributes,
                                       int maturityLevelsCount) {
}
