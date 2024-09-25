package org.flickit.assessment.core.application.service.subjectinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.SubjectInsight;
import org.flickit.assessment.core.application.port.in.subjectinsight.GetSubjectInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectReportInfoPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.LoadSubjectInsightPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_SUBJECT_INSIGHT;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.MessageKey.SUBJECT_DEFAULT_INSIGHT;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetSubjectInsightService implements GetSubjectInsightUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadSubjectInsightPort loadSubjectInsightPort;
    private final LoadSubjectReportInfoPort loadSubjectReportInfoPort;

    @Override
    public Result getSubjectInsight(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        validateAssessmentResultPort.validate(param.getAssessmentId());

        var editable = assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT);

        var subjectInsight = loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId());

        return subjectInsight.map(insight -> getAssessorInsight(assessmentResult, insight, editable))
            .orElseGet(() -> getDefaultInsight(param.getAssessmentId(), param.getSubjectId(), editable));

    }

    private Result getAssessorInsight(AssessmentResult assessmentResult, SubjectInsight subjectInsight, boolean editable) {
        return new Result(null,
            new Result.AssessorInsight(subjectInsight.getInsight(),
                subjectInsight.getInsightTime(),
                assessmentResult.getLastCalculationTime().isBefore(subjectInsight.getInsightTime())),
            editable);
    }

    private Result getDefaultInsight(UUID assessmentId, long subjectId, boolean editable) {
        return new Result(new Result.DefaultInsight(createDefaultInsight(assessmentId, subjectId)),
            null,
            editable);
    }

    String createDefaultInsight(UUID assessmentId, long subjectId) {
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
