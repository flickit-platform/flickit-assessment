package org.flickit.assessment.core.adapter.in.rest.assessment;

import org.flickit.assessment.core.application.domain.report.AssessmentReport.AssessmentReportItem;
import org.flickit.assessment.core.application.domain.report.AssessmentReport.SubjectReportItem;
import org.flickit.assessment.core.application.port.in.assessment.ReportAssessmentUseCase.Result.TopAttribute;

import java.util.List;

public record AssessmentReportResponseDto(
    AssessmentReportItem assessment,
    List<TopAttribute> topStrengths,
    List<TopAttribute> topWeaknesses,
    List<SubjectReportItem> subjects
) {}
