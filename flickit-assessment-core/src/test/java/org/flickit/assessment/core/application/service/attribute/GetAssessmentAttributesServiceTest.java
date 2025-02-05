package org.flickit.assessment.core.application.service.attribute;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.attribute.GetAssessmentAttributesUseCase;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributesPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_ATTRIBUTES;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssessmentAttributesServiceTest {

    @InjectMocks
    private GetAssessmentAttributesService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAttributesPort loadAttributesPort;

    private final LoadAttributesPort.Result result1 = new LoadAttributesPort.Result(1766L, "Team Agility", "How?", 1, 1, 0.0,
        new LoadAttributesPort.MaturityLevel(1991L, "Unprepared", "Tools are insufficient.", 1, 1),
        new LoadAttributesPort.Subject(463L, "Team"));

    private final LoadAttributesPort.Result result2 = new LoadAttributesPort.Result(1769L, "Software Reliability", "How?", 2, 3, 11.22,
        new LoadAttributesPort.MaturityLevel(1991L, "Unprepared", "causing frequent issues and inefficiencies.", 4, 5),
        new LoadAttributesPort.Subject(464L, "Software"));

    List<LoadAttributesPort.Result> portResult = List.of(result1, result2);


    @Test
    void testGetAssessmentAttributesService_whenCurrentUserDoesNotHaveAccess_thenThrowsAccessDeniedException() {
        var param = createParam(GetAssessmentAttributesUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_ATTRIBUTES))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentAttributes(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testGetAssessmentAttributesService_whenNoAttributesExist_thenReturnEmptyResult() {
        var param = createParam(GetAssessmentAttributesUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_ATTRIBUTES))
            .thenReturn(true);
        when(loadAttributesPort.loadAttributes(param.getAssessmentId()))
            .thenReturn(List.of());

        var result = service.getAssessmentAttributes(param);
        assertNotNull(result);
        assertNull(result.attributes());
    }

    @Test
    void testGetAssessmentAttributesService_whenAttributesExist_thenReturnResult() {
        var param = createParam(GetAssessmentAttributesUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_ATTRIBUTES))
            .thenReturn(true);
        when(loadAttributesPort.loadAttributes(param.getAssessmentId()))
            .thenReturn(portResult);

        var result = service.getAssessmentAttributes(param);
        assertNotNull(result);
        assertNotNull(result.attributes());

        assertThat(result.attributes())
            .zipSatisfy(portResult, (actual, expected) -> {
                assertEquals(expected.id(), actual.id());
                assertEquals(expected.title(), actual.title());
                assertEquals(expected.description(), actual.description());
                assertEquals(expected.index(), actual.index());
                assertEquals(expected.weight(), actual.weight());
                assertEquals(expected.confidenceValue(), actual.confidenceValue());

                assertEquals(expected.maturityLevel().id(), actual.maturityLevel().id());
                assertEquals(expected.maturityLevel().title(), actual.maturityLevel().title());
                assertEquals(expected.maturityLevel().description(), actual.maturityLevel().description());
                assertEquals(expected.maturityLevel().index(), actual.maturityLevel().index());
                assertEquals(expected.maturityLevel().value(), actual.maturityLevel().value());

                assertEquals(expected.subject().id(), actual.subject().id());
                assertEquals(expected.subject().title(), actual.subject().title());
            });
    }

    private GetAssessmentAttributesUseCase.Param createParam(Consumer<GetAssessmentAttributesUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAssessmentAttributesUseCase.Param.ParamBuilder paramBuilder() {
        return GetAssessmentAttributesUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
