package org.flickit.assessment.core.application.service.subjectinsight;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.SubjectInsight;
import org.flickit.assessment.core.application.domain.report.SubjectReportItem;
import org.flickit.assessment.core.application.port.in.subjectinsight.InitSubjectInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectReportInfoPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.CreateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.LoadSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.UpdateSubjectInsightPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.flickit.assessment.core.test.fixture.application.SubjectInsightMother;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.INIT_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.MessageKey.SUBJECT_DEFAULT_INSIGHT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitSubjectInsightServiceTest {

    @InjectMocks
    private InitSubjectInsightService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private CreateSubjectInsightPort createSubjectInsightPort;

    @Mock
    private UpdateSubjectInsightPort updateSubjectInsightPort;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadSubjectReportInfoPort loadSubjectReportInfoPort;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private LoadSubjectInsightPort loadSubjectInsightPort;

    @Captor
    private ArgumentCaptor<SubjectInsight> subjectInsightArgumentCaptor;

    @Test
    void testInitSubjectInsight_whenCurrentUserDoesNotHaveRequiredPermission_throwAccessDeniedException() {
        var param = createParam(InitSubjectInsightUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
                .thenReturn(false);

        var throwable = Assertions.assertThrows(AccessDeniedException.class, () -> service.initSubjectInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort,
                createSubjectInsightPort,
                updateSubjectInsightPort,
                validateAssessmentResultPort,
                loadSubjectInsightPort);
    }

    @Test
    void testInitSubjectInsight_whenAssessmentResultOfRequestedAssessmentNotExist_thenThrowResourceNotFoundException() {
        var param = createParam(InitSubjectInsightUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
                .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.initSubjectInsight(param));
        assertEquals(INIT_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadSubjectReportInfoPort,
            createSubjectInsightPort,
            updateSubjectInsightPort,
            validateAssessmentResultPort,
            loadSubjectInsightPort);
    }

    @Test
    void testInitSubjectInsight_whenSubjectInsightExists_thenUpdateSubjectInsight() {
        var param = createParam(InitSubjectInsightUseCase.Param.ParamBuilder::build);
        AssessmentResult assessmentResult = AssessmentResultMother.validResult();
        var mLevel = MaturityLevelMother.levelOne();
        var subject = new SubjectReportItem(1L,
            "reportTitle",
            "desc",
            mLevel,
            1d,
            true,
            true);
        var subjectReport = new LoadSubjectReportInfoPort.Result(subject, List.of(mLevel), new ArrayList<>());
        var insight = MessageBundle.message(SUBJECT_DEFAULT_INSIGHT,
            subject.title(),
            subject.description(),
            subject.confidenceValue() != null ? (int) Math.ceil(subject.confidenceValue()) : 0,
            subject.title(),
            subject.maturityLevel().getIndex(),
            subjectReport.maturityLevels().size(),
            subject.maturityLevel().getTitle(),
            subjectReport.attributes().size(),
            subject.title());
        var subjectInsight = SubjectInsightMother.subjectInsight();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId()))
            .thenReturn(Optional.of(subjectInsight));
        when(loadSubjectReportInfoPort.load(param.getAssessmentId(), param.getSubjectId()))
            .thenReturn(subjectReport);
        doNothing().when(updateSubjectInsightPort).update(any(SubjectInsight.class));

        service.initSubjectInsight(param);

        verify(updateSubjectInsightPort).update(subjectInsightArgumentCaptor.capture());
        SubjectInsight capturedSubjectInsight = subjectInsightArgumentCaptor.getValue();
        assertNotNull(capturedSubjectInsight);
        assertEquals(assessmentResult.getId(), capturedSubjectInsight.getAssessmentResultId());
        assertEquals(param.getSubjectId(), capturedSubjectInsight.getSubjectId());
        assertEquals(insight, capturedSubjectInsight.getInsight());
        assertNull(capturedSubjectInsight.getInsightBy());
        assertNotNull(capturedSubjectInsight.getInsightTime());
        assertFalse(capturedSubjectInsight.isApproved());

        verifyNoInteractions(createSubjectInsightPort);
    }

    @Test
    void testInitSubjectInsight_whenRequestedToInitiateSubjectInsightOfExistingAssessmentResult_thenCreateDefaultSubjectInsight() {
        var param = createParam(InitSubjectInsightUseCase.Param.ParamBuilder::build);
        AssessmentResult assessmentResult = AssessmentResultMother.validResult();
        var mLevel = MaturityLevelMother.levelOne();
        var subject = new SubjectReportItem(1L,
            "reportTitle",
            "desc",
            mLevel,
            1d,
            true,
            true);
        var subjectReport = new LoadSubjectReportInfoPort.Result(subject, List.of(mLevel), new ArrayList<>());
        var insight = MessageBundle.message(SUBJECT_DEFAULT_INSIGHT,
            subject.title(),
            subject.description(),
            subject.confidenceValue() != null ? (int) Math.ceil(subject.confidenceValue()) : 0,
            subject.title(),
            subject.maturityLevel().getIndex(),
            subjectReport.maturityLevels().size(),
            subject.maturityLevel().getTitle(),
            subjectReport.attributes().size(),
            subject.title());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
                .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId())).thenReturn(Optional.empty());
        when(loadSubjectReportInfoPort.load(param.getAssessmentId(), param.getSubjectId())).thenReturn(subjectReport);
        doNothing().when(createSubjectInsightPort).persist(any(SubjectInsight.class));

        service.initSubjectInsight(param);

        verify(createSubjectInsightPort).persist(subjectInsightArgumentCaptor.capture());
        SubjectInsight subjectInsight = subjectInsightArgumentCaptor.getValue();
        assertNotNull(subjectInsight);
        assertEquals(assessmentResult.getId(), subjectInsight.getAssessmentResultId());
        assertEquals(param.getSubjectId(), subjectInsight.getSubjectId());
        assertEquals(insight, subjectInsight.getInsight());
        assertNull(subjectInsight.getInsightBy());
        assertNotNull(subjectInsight.getInsightTime());
        assertFalse(subjectInsight.isApproved());

        verifyNoInteractions(updateSubjectInsightPort);
    }

    private InitSubjectInsightUseCase.Param createParam(Consumer<InitSubjectInsightUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private InitSubjectInsightUseCase.Param.ParamBuilder paramBuilder() {
        return InitSubjectInsightUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .subjectId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
