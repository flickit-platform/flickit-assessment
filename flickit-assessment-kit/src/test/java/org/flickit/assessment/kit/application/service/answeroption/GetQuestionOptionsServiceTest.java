package org.flickit.assessment.kit.application.service.answeroption;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.answeroption.GetQuestionOptionsUseCase;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionsByQuestionPort;
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
    private LoadAnswerOptionsByQuestionPort loadAnswerOptionsByQuestionPort;

    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testGetQuestionOptions_WhenCurrentUserIsNotExpertGroupMember_ThenThrowAccessDeniedException() {
        var param = createParam(GetQuestionOptionsUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId()))
            .thenReturn(false);

        AccessDeniedException throwable = assertThrows(AccessDeniedException.class, () -> service.getQuestionOptions(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAnswerOptionsByQuestionPort);
    }

    @Test
    void testGetQuestionOptions_WhenCurrentUserIsExpertGroupMember_ThenGetQuestionOptions() {
        var param = createParam(GetQuestionOptionsUseCase.Param.ParamBuilder::build);
        var answerOptionA = createAnswerOption("titleA", 1);
        var answerOptionB = createAnswerOption("titleB", 2);
        List<AnswerOption> expectedAnswerOptions = List.of(answerOptionA, answerOptionB);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId()))
            .thenReturn(true);
        when(loadAnswerOptionsByQuestionPort.loadByQuestionId(param.getQuestionId(), param.getKitVersionId()))
            .thenReturn(expectedAnswerOptions);

        var result = service.getQuestionOptions(param);
        assertNotNull(result);
        assertEquals(expectedAnswerOptions.size(), result.answerOptions().size());
        for (int i = 0; i < result.answerOptions().size(); i++) {
            assertEquals(expectedAnswerOptions.get(i).getId(), result.answerOptions().get(i).id());
            assertEquals(expectedAnswerOptions.get(i).getIndex(), result.answerOptions().get(i).index());
            assertEquals(expectedAnswerOptions.get(i).getTitle(), result.answerOptions().get(i).title());
        }
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
