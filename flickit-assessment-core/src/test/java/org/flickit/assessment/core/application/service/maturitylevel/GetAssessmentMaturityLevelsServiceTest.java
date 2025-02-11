package org.flickit.assessment.core.application.service.maturitylevel;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.port.in.maturitylevel.GetAssessmentMaturityLevelsUseCase;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentMother;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_MATURITY_LEVELS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_MATURITY_LEVELS_ASSESSMENT_NOT_FOUND;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAssessmentMaturityLevelsServiceTest {

    @InjectMocks
    private GetAssessmentMaturityLevelsService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentPort loadAssessmentPort;

    @Mock
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Test
    void testGetAssessmentMaturityLevels_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowsAccessDeniedException() {
        var param = createParam(GetAssessmentMaturityLevelsUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_MATURITY_LEVELS))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentMaturityLevels(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadMaturityLevelsPort,
            loadAssessmentPort);
    }

    @Test
    void testGetAssessmentMaturityLevels_whenAssessmentNotFound_thenThrowsResourceNotFoundException() {
        var param = createParam(GetAssessmentMaturityLevelsUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_MATURITY_LEVELS))
            .thenReturn(true);
        when(loadAssessmentPort.getAssessmentById(param.getAssessmentId()))
            .thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getAssessmentMaturityLevels(param));
        assertEquals(GET_ASSESSMENT_MATURITY_LEVELS_ASSESSMENT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(
            loadMaturityLevelsPort);
    }

    @Test
    void testGetAssessmentMaturityLevels_whenParamsAreValid_thenReturnMaturityLevels() {
        var param = createParam(GetAssessmentMaturityLevelsUseCase.Param.ParamBuilder::build);
        var maturityLevels = List.of(MaturityLevelMother.levelOne(), MaturityLevelMother.levelTwo());
        Assessment assessment = AssessmentMother.assessment();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_MATURITY_LEVELS))
            .thenReturn(true);
        when(loadAssessmentPort.getAssessmentById(param.getAssessmentId()))
            .thenReturn(Optional.of(assessment));
        when(loadMaturityLevelsPort.loadByKitVersionId(assessment.getAssessmentKit().getKitVersion())).thenReturn(maturityLevels);

        var result = service.getAssessmentMaturityLevels(param);
        assertThat(result.maturityLevels())
            .zipSatisfy(maturityLevels, (actual, expected) -> {
                assertEquals(expected.getId(), actual.id());
                assertEquals(expected.getTitle(), actual.title());
                assertEquals(expected.getDescription(), actual.description());
                assertEquals(expected.getValue(), actual.value());
                assertEquals(expected.getIndex(), actual.index());
            });
    }

    private GetAssessmentMaturityLevelsUseCase.Param createParam(Consumer<GetAssessmentMaturityLevelsUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAssessmentMaturityLevelsUseCase.Param.ParamBuilder paramBuilder() {
        return GetAssessmentMaturityLevelsUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
