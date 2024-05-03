package org.flickit.assessment.core.adapter.in.rest.subject;

import org.flickit.assessment.core.application.domain.report.SubjectReport;
import org.flickit.assessment.core.application.port.in.subject.ReportSubjectUseCase;

import java.util.List;

public record SubjectReportResponseDto(SubjectReport.SubjectReportItem subject,
                                       List<ReportSubjectUseCase.Result.TopAttribute> topStrengths,
                                       List<ReportSubjectUseCase.Result.TopAttribute> topWeaknesses,
                                       List<SubjectReport.AttributeReportItem> attributes,
                                       int maturityLevelsCount) {
}
