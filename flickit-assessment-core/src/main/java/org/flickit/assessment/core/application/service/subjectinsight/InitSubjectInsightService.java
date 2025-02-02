package org.flickit.assessment.core.application.service.subjectinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.SubjectInsight;
import org.flickit.assessment.core.application.port.in.subjectinsight.InitSubjectInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectReportInfoPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.CreateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.LoadSubjectInsightPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.INIT_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.INIT_SUBJECT_INSIGHT_INSIGHT_DUPLICATE;
import static org.flickit.assessment.core.common.MessageKey.SUBJECT_DEFAULT_INSIGHT;

@Service
@Transactional
@RequiredArgsConstructor
public class InitSubjectInsightService implements InitSubjectInsightUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final CreateSubjectInsightPort createSubjectInsightPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadSubjectReportInfoPort loadSubjectReportInfoPort;
    private final LoadSubjectInsightPort loadSubjectInsightPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;

    @Override
    public void initSubjectInsight(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResultId = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .map(AssessmentResult::getId)
            .orElseThrow(() -> new ResourceNotFoundException(INIT_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND));
        validateAssessmentResultPort.validate(param.getAssessmentId());

        var subjectInsightOptional = loadSubjectInsightPort.load(assessmentResultId, param.getSubjectId());
        if (subjectInsightOptional.isPresent() && subjectInsightOptional.get().getInsightBy() != null)
            throw new ValidationException(INIT_SUBJECT_INSIGHT_INSIGHT_DUPLICATE);

        String defaultInsight = createDefaultInsight(param.getAssessmentId(), param.getSubjectId());
        var subjectInsight = new SubjectInsight(assessmentResultId,
            param.getSubjectId(),
            defaultInsight,
            LocalDateTime.now(),
            LocalDateTime.now(),
            null,
            false);

        createSubjectInsightPort.persist(subjectInsight);
    }

    private String createDefaultInsight(UUID assessmentId, long subjectId) {
        var subjectReport = loadSubjectReportInfoPort.load(assessmentId, subjectId);
        var subject = subjectReport.subject();

        return MessageBundle.message(SUBJECT_DEFAULT_INSIGHT,
            subject.title(),
            subject.description(),
            subject.confidenceValue() != null ? (int) Math.ceil(subject.confidenceValue()) : 0,
            subject.title(),
            subject.maturityLevel().getIndex(),
            subjectReport.maturityLevels().size(),
            subject.maturityLevel().getTitle(),
            subjectReport.attributes().size(),
            subject.title());
    }
}
