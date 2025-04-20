package org.flickit.assessment.kit.application.service.answeroption;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.answeroption.GetQuestionOptionsUseCase;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionsPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionMother.createAnswerOption;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetQuestionOptionsServiceTest {

    @InjectMocks
    private GetQuestionOptionsService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadAnswerOptionsPort loadAnswerOptionsPort;

    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testGetQuestionOptions_whenCurrentUserIsNotExpertGroupMember_thenThrowAccessDeniedException() {
        var param = createParam(GetQuestionOptionsUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId()))
            .thenReturn(false);

        AccessDeniedException throwable = assertThrows(AccessDeniedException.class, () -> service.getQuestionOptions(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAnswerOptionsPort);
    }

    @Test
    void testGetQuestionOptions_whenCurrentUserIsExpertGroupMember_thenGetQuestionOptions() {
        var param = createParam(GetQuestionOptionsUseCase.Param.ParamBuilder::build);
        var answerRangeId = 8329L;
        var answerOptionA = createAnswerOption(answerRangeId, "titleA", 1);
        var answerOptionB = createAnswerOption(answerRangeId, "titleB", 2);
        List<AnswerOption> expectedAnswerOptions = List.of(answerOptionA, answerOptionB);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId()))
            .thenReturn(true);
        when(loadAnswerOptionsPort.loadByQuestionId(param.getQuestionId(), param.getKitVersionId()))
            .thenReturn(expectedAnswerOptions);

        var result = service.getQuestionOptions(param);
        assertNotNull(result);
        assertEquals(expectedAnswerOptions.size(), result.answerOptions().size());
        assertThat(result.answerOptions())
            .zipSatisfy(expectedAnswerOptions, (actual, expected) -> {
                assertEquals(expected.getId(), actual.id());
                assertEquals(expected.getIndex(), actual.index());
                assertEquals(expected.getTitle(), actual.title());
                assertEquals(expected.getValue(), actual.value());
                assertEquals(expected.getTranslations(), actual.translations());
            });
    }

    private GetQuestionOptionsUseCase.Param createParam(Consumer<GetQuestionOptionsUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetQuestionOptionsUseCase.Param.ParamBuilder paramBuilder() {
        return GetQuestionOptionsUseCase.Param.builder()
            .kitVersionId(1L)
            .questionId(2L)
            .currentUserId(UUID.randomUUID());
    }
}
