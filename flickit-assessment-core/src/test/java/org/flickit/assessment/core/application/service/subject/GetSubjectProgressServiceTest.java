package org.flickit.assessment.core.application.service.subject;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.port.in.subject.GetSubjectProgressUseCase;
import org.flickit.assessment.core.application.port.out.answer.CountAnswersByQuestionIdsPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionsBySubjectPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectPort;
import org.flickit.assessment.core.test.fixture.application.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_SUBJECT_PROGRESS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSubjectProgressServiceTest {

    @InjectMocks
    private GetSubjectProgressService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadQuestionsBySubjectPort loadQuestionsBySubjectPort;

    @Mock
    private LoadSubjectPort loadSubjectPort;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private CountAnswersByQuestionIdsPort countAnswersByQuestionIdsPort;

    @Test
    void testGetSubjectProgress_ValidResult() {
        UUID currentUserId = UUID.randomUUID();
        var questions = List.of(
            QuestionMother.withNoImpact(),
            QuestionMother.withNoImpact(),
            QuestionMother.withNoImpact()
        );
        var questionIds = questions.stream()
            .map(Question::getId)
            .toList();
        var qav = AttributeValueMother.hasFullScoreOnLevel23WithWeight(1, 1533);
        var subjectValue = SubjectValueMother.withAttributeValues(List.of(qav), 1);
        var result = AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevel(
            List.of(subjectValue), MaturityLevelMother.levelTwo());

        when(assessmentAccessChecker.isAuthorized(result.getAssessment().getId(), currentUserId, VIEW_SUBJECT_PROGRESS)).thenReturn(true);
        when(loadQuestionsBySubjectPort.loadQuestionsBySubject(subjectValue.getSubject().getId(), result.getKitVersionId())).
            thenReturn(questions);
        when(loadSubjectPort.loadByIdAndKitVersionId(subjectValue.getSubject().getId(), result.getKitVersionId())).
            thenReturn(Optional.of(subjectValue.getSubject()));
        when(loadAssessmentResultPort.loadByAssessmentId(result.getAssessment().getId())).thenReturn(Optional.of(result));
        when(countAnswersByQuestionIdsPort.countByQuestionIds(
            result.getId(), questionIds)).thenReturn(2);

        var subjectProgress = service.getSubjectProgress(new GetSubjectProgressUseCase.Param(
            result.getAssessment().getId(), subjectValue.getSubject().getId(), currentUserId));

        assertFalse(subjectProgress.title().isBlank());
        assertEquals(2, subjectProgress.answerCount());
        assertEquals(3, subjectProgress.questionCount());
    }

    @Test
    void testGetSubjectProgress_UserIsNotAuthorized_ThrowsException() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        GetSubjectProgressUseCase.Param param = new GetSubjectProgressUseCase.Param(
            assessmentId, 1L, currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), currentUserId, VIEW_SUBJECT_PROGRESS)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getSubjectProgress(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }
}
