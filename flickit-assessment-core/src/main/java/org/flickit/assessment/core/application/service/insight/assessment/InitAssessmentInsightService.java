package org.flickit.assessment.core.application.service.insight.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.insight.AssessmentInsight;
import org.flickit.assessment.core.application.port.in.insight.assessment.InitAssessmentInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.CreateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.UpdateAssessmentInsightPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.INIT_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class InitAssessmentInsightService implements InitAssessmentInsightUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final CreateAssessmentInsightHelper createAssessmentInsightHelper;
    private final LoadAssessmentInsightPort loadAssessmentInsightPort;
    private final UpdateAssessmentInsightPort updateAssessmentInsightPort;
    private final CreateAssessmentInsightPort createAssessmentInsightPort;

    @Override
    public void initAssessmentInsight(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(INIT_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND));
        validateAssessmentResultPort.validate(param.getAssessmentId());

        var locale = Locale.of(assessmentResult.getLanguage().getCode());
        var assessmentInsight = createAssessmentInsightHelper.createAssessmentInsight(assessmentResult, locale);

        loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())
            .ifPresentOrElse(
                insight -> updateAssessmentInsightPort.updateInsight(assignIdToInsight(insight.getId(), assessmentInsight)),
                () -> createAssessmentInsightPort.persist(assessmentInsight)
            );
    }

    private AssessmentInsight assignIdToInsight(UUID assessmentInsightId, AssessmentInsight assessmentInsight) {
        return new AssessmentInsight(assessmentInsightId,
            assessmentInsight.getAssessmentResultId(),
            assessmentInsight.getInsight(),
            assessmentInsight.getInsightTime(),
            assessmentInsight.getLastModificationTime(),
            null,
            false);
    }
}
