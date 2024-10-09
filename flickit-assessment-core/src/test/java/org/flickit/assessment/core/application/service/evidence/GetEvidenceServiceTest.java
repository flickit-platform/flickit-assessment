package org.flickit.assessment.core.application.service.evidence;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.domain.User;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceUseCase.Param;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerPort;
import org.flickit.assessment.core.application.port.out.answeroption.LoadAnswerOptionsByQuestionPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.flickit.assessment.core.test.fixture.application.AnswerMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.EvidenceMother;
import org.flickit.assessment.core.test.fixture.application.QuestionMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_EVIDENCE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_EVIDENCE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetEvidenceServiceTest {

    @InjectMocks
    private GetEvidenceService service;

    @Mock
    private LoadEvidencePort loadEvidencePort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadAnswerOptionsByQuestionPort loadAnswerOptionsByQuestionPort;

    @Mock
    private LoadQuestionPort loadQuestionPort;

    @Mock
    private LoadAnswerPort loadAnswerPort;

    @Test
    @DisplayName("If the evidence does not exist, then throw notFoundException.")
    void testLoadEvidence_evidenceNotExist_NotFoundException() {
        var id = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(id, currentUserId);

        when(loadEvidencePort.loadNotDeletedEvidence(id)).thenThrow(new ResourceNotFoundException(GET_EVIDENCE_ID_NOT_NULL));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getEvidence(param));
        assertEquals(GET_EVIDENCE_ID_NOT_NULL, throwable.getMessage());

        verify(loadEvidencePort).loadNotDeletedEvidence(id);
    }

    @Test
    @DisplayName("If currentUser doesn't have the required permission, then throw accessDeniedException.")
    void testLoadEvidence_userDoesNotHaveAccess_AccessDeniedException() {
        var id = UUID.randomUUID();
        var evidence = EvidenceMother.simpleEvidence();
        var currentUserId = UUID.randomUUID();
        var param = new Param(id, currentUserId);

        when(loadEvidencePort.loadNotDeletedEvidence(id)).thenReturn(evidence);
        when(assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), currentUserId, VIEW_EVIDENCE)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getEvidence(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(loadEvidencePort).loadNotDeletedEvidence(id);
        verify(assessmentAccessChecker).isAuthorized(evidence.getAssessmentId(), currentUserId, VIEW_EVIDENCE);
    }

    @Test
    @DisplayName("If parameters are valid and currentUser has the required permission, then return a valid response.")
    void testLoadEvidence_ValidInput_Successful() {
        var id = UUID.randomUUID();
        var evidence = EvidenceMother.simpleEvidence();
        var currentUserId = UUID.randomUUID();
        var param = new Param(id, currentUserId);
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        Question question = QuestionMother.withOptions();
        User user = new User(evidence.getCreatedById(), "displayName", "user@mail.com");
        Answer answer = AnswerMother.answerWithNullNotApplicable(question.getOptions().getFirst());

        when(loadEvidencePort.loadNotDeletedEvidence(id)).thenReturn(evidence);
        when(assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), currentUserId, VIEW_EVIDENCE)).thenReturn(true);
        when(loadUserPort.loadById(evidence.getCreatedById())).thenReturn(Optional.of(user));
        when(loadAssessmentResultPort.loadByAssessmentId(evidence.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerOptionsByQuestionPort.loadByQuestionId(evidence.getQuestionId(), assessmentResult.getKitVersionId())).thenReturn(question.getOptions());
        when(loadQuestionPort.loadByIdAndKitVersionId(evidence.getQuestionId(), assessmentResult.getKitVersionId())).thenReturn(question);
        when(loadAnswerPort.load(assessmentResult.getId(), question.getId())).thenReturn(Optional.of(answer));

        var result = assertDoesNotThrow(() -> service.getEvidence(param));

        assertEquals(evidence.getId(), result.evidence().getId());
        assertEquals(evidence.getType(), result.evidence().getType());
        assertEquals(evidence.getDescription(), result.evidence().getDescription());
        assertEquals(evidence.getCreatedById(), result.user().getId());
        assertEquals(user.getDisplayName(), result.user().getDisplayName());
        assertEquals(evidence.getCreationTime(), result.evidence().getCreationTime());
        assertEquals(evidence.getLastModificationTime(), result.evidence().getLastModificationTime());
        assertEquals(question.getId(), result.question().getId());
        assertEquals(question.getTitle(), result.question().getTitle());
        assertEquals(question.getIndex(), result.question().getIndex());
        assertEquals(question.getOptions(), result.question().getOptions());
        assertEquals(question.getQuestionnaire(), result.question().getQuestionnaire());
        assertNotNull(result.answer());
        assertEquals(answer.getSelectedOption().getId(), result.answer().selectedOption().id());
        assertNull(result.answer().isNotApplicable());
        assertEquals(answer.getConfidenceLevelId(), result.answer().confidenceLevel().getId());
    }
}
