package org.flickit.assessment.core.application.service.subjectinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.SubjectInsight;
import org.flickit.assessment.core.application.port.in.subjectinsight.InitSubjectInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.CreateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.LoadSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.UpdateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.LoadSubjectValuePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.INIT_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.MessageKey.SUBJECT_DEFAULT_INSIGHT;

@Service
@Transactional
@RequiredArgsConstructor
public class InitSubjectInsightService implements InitSubjectInsightUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final CreateSubjectInsightPort createSubjectInsightPort;
    private final UpdateSubjectInsightPort updateSubjectInsightPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadSubjectInsightPort loadSubjectInsightPort;
    private final LoadSubjectValuePort loadSubjectValuePort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;

    @Override
    public void initSubjectInsight(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(INIT_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND));
        validateAssessmentResultPort.validate(param.getAssessmentId());

        String defaultInsight = buildDefaultInsight(param.getSubjectId(), assessmentResult.getId(), assessmentResult.getKitVersionId());
        var subjectInsight = new SubjectInsight(assessmentResult.getId(),
            param.getSubjectId(),
            defaultInsight,
            LocalDateTime.now(),
            null,
            false);

        loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId())
            .ifPresentOrElse(
                existing -> updateSubjectInsightPort.update(subjectInsight),
                () -> createSubjectInsightPort.persist(subjectInsight)
            );
    }

    private String buildDefaultInsight(long subjectId, UUID assessmentResultId, long kitVersionId) {
        var subjectValue = loadSubjectValuePort.load(subjectId, assessmentResultId);

        return MessageBundle.message(SUBJECT_DEFAULT_INSIGHT,
            subjectValue.getSubject().getTitle(),
            subjectValue.getSubject().getDescription(),
            subjectValue.getConfidenceValue() != null ? (int) Math.ceil(subjectValue.getConfidenceValue()) : 0,
            subjectValue.getSubject().getTitle(),
            subjectValue.getMaturityLevel().getIndex(),
            loadMaturityLevelsPort.loadByKitVersionId(kitVersionId).size(),
            subjectValue.getMaturityLevel().getTitle(),
            subjectValue.getSubject().getAttributes().size(),
            subjectValue.getSubject().getTitle());
    }
}
