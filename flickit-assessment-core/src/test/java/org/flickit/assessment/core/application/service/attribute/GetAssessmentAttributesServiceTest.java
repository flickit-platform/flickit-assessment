package org.flickit.assessment.core.application.service.attribute;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAssessmentAttributesServiceTest {

    @InjectMocks
    private GetAssessmentAttributesService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAttributesPort loadAttributesPort;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Test
    void testGetAssessmentAttributes_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        var param = createParam(GetAssessmentAttributesUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_ATTRIBUTES))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentAttributes(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAttributesPort, validateAssessmentResultPort);
    }

    @Test
    void testGetAssessmentAttributes_whenAttributesExist_thenReturnResult() {
        var param = createParam(GetAssessmentAttributesUseCase.Param.ParamBuilder::build);

        LoadAttributesPort.Result attribute1 = createAttribute(1);
        LoadAttributesPort.Result attribute2 = createAttribute(2);

        List<LoadAttributesPort.Result> portResult = List.of(attribute1, attribute2);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_ATTRIBUTES))
            .thenReturn(true);
        when(loadAttributesPort.loadAll(param.getAssessmentId()))
            .thenReturn(portResult);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());

        var result = service.getAssessmentAttributes(param);
        assertNotNull(result);
        assertNotNull(result.attributes());
        assertEquals(portResult.size(), result.attributes().size());

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

    private static LoadAttributesPort.Result createAttribute(int index) {
        return new LoadAttributesPort.Result(1769L + index,
            "Software Reliability" + index,
            "How?",
            index,
            3 + index,
            11.22 + index,
            new LoadAttributesPort.MaturityLevel(1991L + index,
                "Unprepared" + index,
                "causing frequent issues and inefficiencies." + index, 4, 4),
            new LoadAttributesPort.Subject(464L + index, "Software" + index));
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
