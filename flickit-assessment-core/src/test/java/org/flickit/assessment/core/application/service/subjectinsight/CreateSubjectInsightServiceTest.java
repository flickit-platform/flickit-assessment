package org.flickit.assessment.core.application.service.subjectinsight;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.SubjectInsight;
import org.flickit.assessment.core.application.port.in.subjectinsight.CreateSubjectInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.CreateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.LoadSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.UpdateSubjectInsightPort;
import org.flickit.assessment.core.test.fixture.application.SubjectInsightMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_SUBJECT_INSIGHT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResult;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateSubjectInsightServiceTest {

    @InjectMocks
    private CreateSubjectInsightService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadSubjectInsightPort loadSubjectInsightPort;

    @Mock
    private CreateSubjectInsightPort createSubjectInsightPort;

    @Mock
    private UpdateSubjectInsightPort updateSubjectInsightPort;

    @Test
    void testCreateSubjectInsight_whenCurrentUserDoesNotHasRequiredPermission_thenThrowAccessDeniedException() {
        var param = createParam(CreateSubjectInsightUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> service.createSubjectInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(loadAssessmentResultPort,
            createSubjectInsightPort,
            createSubjectInsightPort,
            updateSubjectInsightPort);
    }

    @Test
    void testCreateSubjectInsight_whenInsightDoesNotExist_thenCreateInsight() {
        var param = createParam(CreateSubjectInsightUseCase.Param.ParamBuilder::build);
        AssessmentResult assessmentResult = validResult();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId())).thenReturn(Optional.empty());

        doNothing().when(createSubjectInsightPort).persist(any());

        service.createSubjectInsight(param);

        var createPortParam = ArgumentCaptor.forClass(SubjectInsight.class);
        verify(createSubjectInsightPort).persist(createPortParam.capture());
        assertEquals(assessmentResult.getId(), createPortParam.getValue().getAssessmentResultId());
        assertEquals(param.getSubjectId(), createPortParam.getValue().getSubjectId());
        assertEquals(param.getInsight(), createPortParam.getValue().getInsight());
        assertEquals(param.getCurrentUserId(), createPortParam.getValue().getInsightBy());
        assertNotNull(createPortParam.getValue().getInsightTime());
        assertTrue(createPortParam.getValue().isApproved());

        verifyNoInteractions(updateSubjectInsightPort);
    }

    @Test
    void testCreateSubjectInsight_whenInsightExists_thenUpdateInsight() {
        var param = createParam(CreateSubjectInsightUseCase.Param.ParamBuilder::build);
        var assessmentResult = validResult();
        SubjectInsight subjectInsight = SubjectInsightMother.subjectInsight();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId())).thenReturn(Optional.of(subjectInsight));

        doNothing().when(updateSubjectInsightPort).update(any());

        service.createSubjectInsight(param);

        var updatePortParam = ArgumentCaptor.forClass(SubjectInsight.class);
        verify(updateSubjectInsightPort).update(updatePortParam.capture());
        assertEquals(assessmentResult.getId(), updatePortParam.getValue().getAssessmentResultId());
        assertEquals(param.getSubjectId(), updatePortParam.getValue().getSubjectId());
        assertEquals(param.getInsight(), updatePortParam.getValue().getInsight());
        assertEquals(param.getCurrentUserId(), updatePortParam.getValue().getInsightBy());
        assertNotNull(updatePortParam.getValue().getInsightTime());
        assertNotNull(updatePortParam.getValue().getLastModificationTime());
        assertTrue(updatePortParam.getValue().isApproved());

        verifyNoInteractions(createSubjectInsightPort);
    }

    private CreateSubjectInsightUseCase.Param createParam(Consumer<CreateSubjectInsightUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private CreateSubjectInsightUseCase.Param.ParamBuilder paramBuilder() {
        return CreateSubjectInsightUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .subjectId(1L)
            .insight("subject insight")
            .currentUserId(UUID.randomUUID());
    }
}
