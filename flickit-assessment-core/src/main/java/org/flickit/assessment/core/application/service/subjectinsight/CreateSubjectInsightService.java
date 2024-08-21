package org.flickit.assessment.core.application.service.subjectinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.subjectinsight.CreateSubjectInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.CheckSubjectInsightExistPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.CreateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.UpdateSubjectInsightPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_SUBJECT_INSIGHT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;


@Service
@Transactional
@RequiredArgsConstructor
public class CreateSubjectInsightService implements CreateSubjectInsightUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final CheckSubjectInsightExistPort checkSubjectInsightExistPort;
    private final CreateSubjectInsightPort createSubjectInsightPort;
    private final UpdateSubjectInsightPort updateSubjectInsightPort;

    @Override
    public void createSubjectInsight(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND));

        boolean exists = checkSubjectInsightExistPort.exists(assessmentResult.getId(), param.getSubjectId());
        if (!exists) {
            createSubjectInsightPort.persist(toCreateParam(assessmentResult.getId(), param.getSubjectId(), param.getInsight(), param.getCurrentUserId()));
        } else {
            updateSubjectInsightPort.update(toUpdateParam(assessmentResult.getId(), param.getSubjectId(), param.getInsight(), param.getCurrentUserId()));
        }
    }

    private CreateSubjectInsightPort.Param toCreateParam(UUID assessmentResultId,
                                                         Long subjectId,
                                                         String insight,
                                                         UUID currentUserId) {
        return new CreateSubjectInsightPort.Param(assessmentResultId,
            subjectId,
            insight,
            LocalDateTime.now(),
            currentUserId);
    }

    private UpdateSubjectInsightPort.Param toUpdateParam(UUID assessmentResultId,
                                                         Long subjectId,
                                                         String insight,
                                                         UUID currentUserId) {
        return new UpdateSubjectInsightPort.Param(assessmentResultId,
            subjectId,
            insight,
            LocalDateTime.now(),
            currentUserId);
    }
}
