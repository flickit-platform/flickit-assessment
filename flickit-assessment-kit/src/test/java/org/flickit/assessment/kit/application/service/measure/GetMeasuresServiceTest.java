package org.flickit.assessment.kit.application.service.measure;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.measure.GetMeasuresUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.measure.LoadMeasurePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.flickit.assessment.kit.test.fixture.application.MeasureMother.measureWithTitle;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetMeasuresServiceTest {

    @InjectMocks
    GetMeasuresService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    LoadMeasurePort loadMeasurePort;

    private final GetMeasuresUseCase.Param param = createParam(GetMeasuresUseCase.Param.ParamBuilder::build);
    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testGetMeasures_whenCurrentUserIsNotMemberOfExpertGroup_thenThrowAccessDeniedException() {
        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId()))
            .thenReturn(false);

        AccessDeniedException throwable = assertThrows(AccessDeniedException.class, () -> service.getMeasures(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadMeasurePort);
    }

    @Test
    void testGetMeasures_whenCurrentUserIsMemberOfExpertGroup_thenGetMeasures() {
        var measure1 = measureWithTitle("title1");
        var measure2 = measureWithTitle("title2");
        var items = List.of(new LoadMeasurePort.Result(measure1, 2),
            new LoadMeasurePort.Result(measure2, 3));
        PaginatedResponse<LoadMeasurePort.Result> pageResult = new PaginatedResponse<>(
            items,
            param.getPage(),
            param.getSize(),
            QuestionnaireJpaEntity.Fields.index,
            Sort.Direction.ASC.name().toLowerCase(),
            items.size()
        );

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId()))
            .thenReturn(true);
        when(loadMeasurePort.loadAll(param.getKitVersionId(), param.getPage(), param.getSize()))
            .thenReturn(pageResult);

        var paginatedResponse = service.getMeasures(param);

        assertNotNull(paginatedResponse);
        assertEquals(pageResult.getItems().size(), paginatedResponse.getItems().size());
        assertThat(paginatedResponse.getItems())
            .zipSatisfy(items, (actual, expected) -> {
                assertEquals(expected.measure(), actual.measure());
                assertEquals(expected.questionsCount(), actual.questionsCount());
            });

        assertEquals(2, paginatedResponse.getTotal());
        assertEquals(param.getSize(), paginatedResponse.getSize());
        assertEquals(param.getPage(), paginatedResponse.getPage());
        assertEquals(pageResult.getSort(), paginatedResponse.getSort());
        assertEquals(pageResult.getOrder(), paginatedResponse.getOrder());
    }

    private GetMeasuresUseCase.Param createParam(Consumer<GetMeasuresUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetMeasuresUseCase.Param.ParamBuilder paramBuilder() {
        return GetMeasuresUseCase.Param.builder()
            .kitVersionId(1L)
            .currentUserId(UUID.randomUUID())
            .page(1)
            .size(10);
    }
}
