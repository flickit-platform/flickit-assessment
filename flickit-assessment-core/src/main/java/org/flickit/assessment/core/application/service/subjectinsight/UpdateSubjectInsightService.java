package org.flickit.assessment.core.application.service.subjectinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.SubjectInsight;
import org.flickit.assessment.core.application.port.in.subjectinsight.UpdateSubjectInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.LoadSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.UpdateSubjectInsightPort;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_SUBJECT_INSIGHT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.SUBJECT_INSIGHT_ID_NOT_FOUND;


@Service
@Transactional
@RequiredArgsConstructor
public class UpdateSubjectInsightService implements UpdateSubjectInsightUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadSubjectInsightPort loadSubjectInsightPort;
    private final UpdateSubjectInsightPort updateSubjectInsightPort;

    @Override
    public void updateSubjectInsight(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResultId = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .map(AssessmentResult::getId)
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND));

        Optional<SubjectInsight> subjectInsight = loadSubjectInsightPort.load(assessmentResultId, param.getSubjectId());

        var insight = toSubjectInsight(assessmentResultId, param);

        if (subjectInsight.isPresent())
            updateSubjectInsightPort.update(insight);
        else
            throw new ResourceNotFoundException(SUBJECT_INSIGHT_ID_NOT_FOUND);
    }

    @NotNull
    private static SubjectInsight toSubjectInsight(UUID assessmentResultId, Param param) {
        return new SubjectInsight(assessmentResultId,
            param.getSubjectId(),
            param.getInsight(),
            LocalDateTime.now(),
            param.getCurrentUserId(),
            true);
    }
}
