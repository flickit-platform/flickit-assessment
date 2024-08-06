package org.flickit.assessment.core.application.service.subject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.report.AttributeReportItem;
import org.flickit.assessment.core.application.domain.report.SubjectAttributeReportItem;
import org.flickit.assessment.core.application.domain.report.TopAttributeResolver;
import org.flickit.assessment.core.application.port.in.subject.ReportSubjectUseCase;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectReportInfoPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_SUBJECT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.application.domain.MaturityLevel.middleLevel;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportSubjectService implements ReportSubjectUseCase {

    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadSubjectReportInfoPort loadSubjectReportInfoPort;
    private final AssessmentAccessChecker assessmentAccessChecker;

    @Override
    public Result reportSubject(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_SUBJECT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        validateAssessmentResultPort.validate(param.getAssessmentId());

        var subjectReport = loadSubjectReportInfoPort.load(param.getAssessmentId(), param.getSubjectId());

        List<MaturityLevel> maturityLevels = subjectReport.maturityLevels().stream()
            .map(e -> new MaturityLevel(e.getId(), e.getTitle(), e.getIndex(), e.getValue(), null, null))
            .toList();
        var midLevelMaturity = middleLevel(maturityLevels);
        List<SubjectAttributeReportItem> attributes = subjectReport.attributes();

        var topAttributes = attributes.stream()
            .map(e -> new AttributeReportItem(e.id(), e.title(), e.description(), e.index(), e.confidenceValue(), e.maturityLevel()))
            .toList();
        TopAttributeResolver topAttributeResolver = new TopAttributeResolver(topAttributes, midLevelMaturity);
        var topStrengths = topAttributeResolver.getTopStrengths();
        var topWeaknesses = topAttributeResolver.getTopWeaknesses();

        return new Result(
            subjectReport.subject(),
            topStrengths,
            topWeaknesses,
            subjectReport.attributes(),
            subjectReport.maturityLevels().size());
    }
}
