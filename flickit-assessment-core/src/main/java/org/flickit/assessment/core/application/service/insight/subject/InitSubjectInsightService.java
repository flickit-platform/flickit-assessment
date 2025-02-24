package org.flickit.assessment.core.application.service.insight.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.insight.subject.InitSubjectInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.insight.subject.CreateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.insight.subject.LoadSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.insight.subject.UpdateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.INIT_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.INIT_SUBJECT_INSIGHT_SUBJECT_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class InitSubjectInsightService implements InitSubjectInsightUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadSubjectPort loadSubjectPort;
    private final CreateSubjectInsightsHelper createSubjectInsightsHelper;
    private final LoadSubjectInsightPort loadSubjectInsightPort;
    private final UpdateSubjectInsightPort updateSubjectInsightPort;
    private final CreateSubjectInsightPort createSubjectInsightPort;

    @Override
    public void initSubjectInsight(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(INIT_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND));
        validateAssessmentResultPort.validate(param.getAssessmentId());

        var subject = loadSubjectPort.loadByIdAndKitVersionId(param.getSubjectId(), assessmentResult.getKitVersionId())
            .orElseThrow(() -> new ResourceNotFoundException(INIT_SUBJECT_INSIGHT_SUBJECT_NOT_FOUND));
        var locale = Locale.of(assessmentResult.getAssessment().getAssessmentKit().getLanguage().getCode());
        var subjectInsight = createSubjectInsightsHelper
            .initSubjectInsights(new CreateSubjectInsightsHelper.Param(assessmentResult, List.of(subject.getId()), locale))
            .getFirst();

        loadSubjectInsightPort.load(assessmentResult.getId(), subject.getId())
            .ifPresentOrElse(
                existing -> updateSubjectInsightPort.update(subjectInsight),
                () -> createSubjectInsightPort.persist(subjectInsight)
            );
    }
}
