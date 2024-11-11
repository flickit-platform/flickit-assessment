package org.flickit.assessment.kit.application.service.answerrange;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.data.jpa.kit.answerrange.AnswerRangeJpaEntity;
import org.flickit.assessment.kit.application.domain.AnswerRange;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.answerrange.GetAnswerRangeListUseCase;
import org.flickit.assessment.kit.application.port.out.answerange.LoadAnswerRangesPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.KitVersionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.AnswerRangeMother.createAnswerRangeWithFourOptions;
import static org.flickit.assessment.kit.test.fixture.application.AnswerRangeMother.createReusableAnswerRangeWithTwoOptions;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAnswerRangeListServiceTest {

    @InjectMocks
    private GetAnswerRangeListService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadAnswerRangesPort loadAnswerRangesPort;

    private final KitVersion kitVersion = KitVersionMother.createKitVersion(AssessmentKitMother.simpleKit());

    @Test
    void testGetAnswerRangeList_CurrentUserDoesNotHaveAccess_ThrowsAccessDeniedException() {
        var param = createParam(GetAnswerRangeListUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAnswerRangeList(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testGetAnswerRangeList_ValidParams_ReturnsPaginatedAnswerRangeWithOptions() {
        var param = createParam(GetAnswerRangeListUseCase.Param.ParamBuilder::build);
        var answerRange1 = createReusableAnswerRangeWithTwoOptions();
        var answerRange2 = createAnswerRangeWithFourOptions();

        PaginatedResponse<AnswerRange> paginatedAnswerRanges = new PaginatedResponse<>(
            List.of(answerRange1, answerRange2),
            param.getPage(),
            param.getSize(),
            AnswerRangeJpaEntity.Fields.creationTime,
            Sort.Direction.ASC.name().toLowerCase(),
            2);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(true);
        when(loadAnswerRangesPort.loadByKitVersionId(param.getKitVersionId(), param.getPage(), param.getSize())).thenReturn(paginatedAnswerRanges);

        var result = service.getAnswerRangeList(param);

        assertEquals(paginatedAnswerRanges.getItems().size(), result.getItems().size());
        assertFalse(result.getItems().isEmpty());
        assertEquals(2, result.getItems().get(0).answerOptions().size());
        assertEquals(4, result.getItems().get(1).answerOptions().size());
        assertEquals(param.getSize(), paginatedAnswerRanges.getSize());
        assertEquals(param.getPage(), paginatedAnswerRanges.getPage());
        assertEquals(2, paginatedAnswerRanges.getTotal());
    }

    @Test
    void testGetAnswerRangeList_ValidParamsWithoutAnswerRanges_ReturnsPaginatedEmptyResponse() {
        var param = createParam(GetAnswerRangeListUseCase.Param.ParamBuilder::build);

        PaginatedResponse<AnswerRange> paginatedAnswerRanges = new PaginatedResponse<>(
            List.of(),
            param.getPage(),
            param.getSize(),
            AnswerRangeJpaEntity.Fields.creationTime,
            Sort.Direction.ASC.name().toLowerCase(),
            0);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(true);
        when(loadAnswerRangesPort.loadByKitVersionId(param.getKitVersionId(), param.getPage(), param.getSize())).thenReturn(paginatedAnswerRanges);

        var result = service.getAnswerRangeList(param);

        assertEquals(paginatedAnswerRanges.getItems().size(), result.getItems().size());
        assertTrue(result.getItems().isEmpty());
        assertEquals(param.getSize(), paginatedAnswerRanges.getSize());
        assertEquals(param.getPage(), paginatedAnswerRanges.getPage());
        assertEquals(0, paginatedAnswerRanges.getTotal());
    }

    private GetAnswerRangeListUseCase.Param createParam(Consumer<GetAnswerRangeListUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAnswerRangeListUseCase.Param.ParamBuilder paramBuilder() {
        return GetAnswerRangeListUseCase.Param.builder()
            .kitVersionId(kitVersion.getId())
            .page(0)
            .size(10)
            .currentUserId(UUID.randomUUID());
    }
}
