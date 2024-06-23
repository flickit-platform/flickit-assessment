package org.flickit.assessment.core.application.service.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.port.in.subject.GetSubjectProgressUseCase;
import org.flickit.assessment.core.application.port.out.answer.CountAnswersByQuestionIdsPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionsBySubjectPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_SUBJECT_PROGRESS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_SUBJECT_PROGRESS_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_SUBJECT_PROGRESS_SUBJECT_ID_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetSubjectProgressService implements GetSubjectProgressUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadQuestionsBySubjectPort loadQuestionsBySubjectPort;
    private final LoadSubjectPort loadSubjectPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final CountAnswersByQuestionIdsPort countAnswersByQuestionIdsPort;

    @Override
    public Result getSubjectProgress(Param param) {
        checkUserAccess(param.getAssessmentId(), param.getCurrentUserId());

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_SUBJECT_PROGRESS_ASSESSMENT_RESULT_NOT_FOUND));

        var subject = loadSubjectPort.loadByIdAndKitVersionId(param.getSubjectId(), assessmentResult.getKitVersionId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_SUBJECT_PROGRESS_SUBJECT_ID_NOT_FOUND));

        var questionIds = loadQuestionsBySubjectPort.loadQuestionsBySubject(param.getSubjectId(), assessmentResult.getKitVersionId())
            .stream()
            .map(Question::getId)
            .toList();
        var questionCount = new HashSet<>(questionIds).size();

        int answerCount = countAnswersByQuestionIdsPort.countByQuestionIds(assessmentResult.getId(), questionIds);
        return new Result(param.getSubjectId(), subject.getTitle(), questionCount, answerCount);
    }

    private void checkUserAccess(UUID assessmentId, UUID currentUserId) {
        if (!assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_SUBJECT_PROGRESS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
