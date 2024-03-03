package org.flickit.assessment.core.application.service.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.domain.Subject;
import org.flickit.assessment.core.application.port.in.subject.GetSubjectProgressUseCase;
import org.flickit.assessment.core.application.port.out.answer.CountAnswersByQuestionIdsPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionsBySubjectPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Objects;

import static org.flickit.assessment.core.common.ErrorMessageKey.GET_SUBJECT_PROGRESS_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_SUBJECT_PROGRESS_SUBJECT_ID_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetSubjectProgressService implements GetSubjectProgressUseCase {

    private final LoadQuestionsBySubjectPort loadQuestionsBySubjectPort;
    private final LoadSubjectPort loadSubjectPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final CountAnswersByQuestionIdsPort countAnswersByQuestionIdsPort;

    @Override
    public Result getSubjectProgress(Param param) {
        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_SUBJECT_PROGRESS_ASSESSMENT_RESULT_NOT_FOUND));
        var subject = loadSubjectPort.loadById(param.getSubjectId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_SUBJECT_PROGRESS_SUBJECT_ID_NOT_FOUND));

        checkSubjectBelongsToAssessment(subject, assessmentResult.getAssessment());

        var questionIds = loadQuestionsBySubjectPort.loadQuestionsBySubject(param.getSubjectId())
            .stream()
            .map(Question::getId)
            .toList();
        var questionCount = new HashSet<>(questionIds).size();

        int answerCount = countAnswersByQuestionIdsPort.countByQuestionIds(assessmentResult.getId(), questionIds);
        return new Result(param.getSubjectId(), subject.getTitle(), questionCount, answerCount);
    }

    private void checkSubjectBelongsToAssessment(Subject subject, Assessment assessment) {
        if (!Objects.equals(subject.getKitId(), assessment.getAssessmentKit().getId())) {
            throw new ResourceNotFoundException(GET_SUBJECT_PROGRESS_SUBJECT_ID_NOT_FOUND);
        }
    }
}
