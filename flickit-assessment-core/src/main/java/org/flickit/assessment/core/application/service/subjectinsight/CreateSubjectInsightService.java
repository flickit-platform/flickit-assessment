package org.flickit.assessment.core.application.service.subjectinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.SubjectInsight;
import org.flickit.assessment.core.application.port.in.subjectinsight.CreateSubjectInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.CreateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.LoadSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.UpdateSubjectInsightPort;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_SUBJECT_INSIGHT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;


@Service
@Transactional
@RequiredArgsConstructor
public class CreateSubjectInsightService implements CreateSubjectInsightUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadSubjectInsightPort loadSubjectInsightPort;
    private final CreateSubjectInsightPort createSubjectInsightPort;
    private final UpdateSubjectInsightPort updateSubjectInsightPort;

    @Override
    public void createSubjectInsight(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND));

        Optional<SubjectInsight> subjectInsight = loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId());

        var insight = toSubjectInsight(param, assessmentResult);

        if (subjectInsight.isPresent())
            updateSubjectInsightPort.update(insight);
        else
            createSubjectInsightPort.persist(insight);
    }

    @NotNull
    private static SubjectInsight toSubjectInsight(Param param, AssessmentResult assessmentResult) {
        return new SubjectInsight(assessmentResult.getId(), param.getSubjectId(), param.getInsight(), LocalDateTime.now(), param.getCurrentUserId());
    }
}
