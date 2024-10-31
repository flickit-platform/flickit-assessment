package org.flickit.assessment.kit.application.service.answerrange;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.data.jpa.kit.answerrange.AnswerRangeJpaEntity;
import org.flickit.assessment.kit.application.domain.AnswerRange;
import org.flickit.assessment.kit.application.port.in.answerrange.GetAnswerRangeListUseCase;
import org.flickit.assessment.kit.application.port.out.answerange.LoadAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.test.fixture.application.AnswerRangeMother;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAnswerRangeListServiceTest {

    @InjectMocks
    GetAnswerRangeListService service;

    @Mock
    LoadKitVersionPort loadKitVersionPort;

    @Mock
    CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    LoadAnswerRangePort loadAnswerRangePort;

    @Test
    void testGetAnswerRangeListService_CurrentUserDoesNotHaveAccess_ThrowsAccessDeniedException() {
        var param = createParam(GetAnswerRangeListUseCase.Param.ParamBuilder::build);
        var kitVersion = KitVersionMother.createKitVersion(AssessmentKitMother.simpleKit());

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(false);
        assertThrows(AccessDeniedException.class, () -> service.getAnswerRangeList(param));
    }

    @Test
    void testGetAnswerRangeListService_ValidParams_ReturnsPaginatedAnswerRangeWithOptions() {
        int page = 0;
        int size = 10;
        var param = createParam(GetAnswerRangeListUseCase.Param.ParamBuilder::build);
        var kitVersion = KitVersionMother.createKitVersion(AssessmentKitMother.simpleKit());
        var answerRange1 = AnswerRangeMother.createAnswerRange();
        var answerRange2 = AnswerRangeMother.createAnswerRange();

        PaginatedResponse<AnswerRange> paginatedAnswerRanges = new PaginatedResponse<>(
            List.of(answerRange1, answerRange2),
            page,
            size,
            AnswerRangeJpaEntity.Fields.creationTime,
            Sort.Direction.ASC.name().toLowerCase(),
            2);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(true);
        when(loadAnswerRangePort.loadByKitVersionId(param.getKitVersionId(), param.getPage(), param.getSize())).thenReturn(paginatedAnswerRanges);

        var result = service.getAnswerRangeList(param);

        assertEquals(paginatedAnswerRanges.getItems().size(), result.getItems().size());
        assertFalse(result.getItems().isEmpty());
        assertEquals(1, result.getItems().get(0).answerOptions().size());
        assertEquals(1, result.getItems().get(1).answerOptions().size());
        assertEquals(size, paginatedAnswerRanges.getSize());
        assertEquals(page, paginatedAnswerRanges.getPage());
        assertEquals(2, paginatedAnswerRanges.getTotal());
    }

    @Test
    void testGetAnswerRangeListService_ValidParamsWithoutAnswerRanges_ReturnsPaginatedEmptyResponse() {
        int page = 0;
        int size = 10;
        var param = createParam(GetAnswerRangeListUseCase.Param.ParamBuilder::build);
        var kitVersion = KitVersionMother.createKitVersion(AssessmentKitMother.simpleKit());

        PaginatedResponse<AnswerRange> paginatedAnswerRanges = new PaginatedResponse<>(
            List.of(),
            page,
            size,
            AnswerRangeJpaEntity.Fields.creationTime,
            Sort.Direction.ASC.name().toLowerCase(),
            0);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(true);
        when(loadAnswerRangePort.loadByKitVersionId(param.getKitVersionId(), param.getPage(), param.getSize())).thenReturn(paginatedAnswerRanges);

        var result = service.getAnswerRangeList(param);

        assertEquals(paginatedAnswerRanges.getItems().size(), result.getItems().size());
        assertTrue(result.getItems().isEmpty());
        assertEquals(size, paginatedAnswerRanges.getSize());
        assertEquals(page, paginatedAnswerRanges.getPage());
        assertEquals(0, paginatedAnswerRanges.getTotal());
    }

    private GetAnswerRangeListUseCase.Param createParam(Consumer<GetAnswerRangeListUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAnswerRangeListUseCase.Param.ParamBuilder paramBuilder() {
        return GetAnswerRangeListUseCase.Param.builder()
            .kitVersionId(1L)
            .page(0)
            .size(10)
            .currentUserId(UUID.randomUUID());
    }
}
