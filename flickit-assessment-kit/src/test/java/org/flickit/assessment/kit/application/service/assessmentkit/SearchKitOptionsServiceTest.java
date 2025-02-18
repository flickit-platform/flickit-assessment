package org.flickit.assessment.kit.application.service.assessmentkit;

import org.assertj.core.api.Assertions;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.port.in.assessmentkit.SearchKitOptionsUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.SearchKitOptionsPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.privateKit;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchKitOptionsServiceTest {

    @InjectMocks
    private SearchKitOptionsService service;

    @Mock
    private SearchKitOptionsPort port;

    @Test
    void testSearchKitOptions_ValidInput_ValidResult() {
        var param = createParam(SearchKitOptionsUseCase.Param.ParamBuilder::build);

        var publicKit = simpleKit();
        var privateKit = privateKit();
        List<AssessmentKit> expectedItems = List.of(publicKit, privateKit);
        PaginatedResponse<AssessmentKit> kitPaginatedResponse = new PaginatedResponse<>(expectedItems,
            param.getPage(),
            param.getSize(),
            Sort.Direction.ASC.name().toLowerCase(),
            "title",
            expectedItems.size());

        when(port.searchKitOptions(any(SearchKitOptionsPort.Param.class))).thenReturn(kitPaginatedResponse);

        var result = service.searchKitOptions(param);

        assertNotNull(result.getItems());
        assertEquals(expectedItems.size(), result.getItems().size());
        assertEquals(param.getPage(), result.getPage());
        assertEquals(param.getSize(), result.getSize());
        assertEquals(kitPaginatedResponse.getOrder(), result.getOrder());
        assertEquals(kitPaginatedResponse.getSort(), result.getSort());
        assertEquals(2, result.getTotal());

        Assertions.assertThat(result.getItems())
            .zipSatisfy(expectedItems, (actual, expected) -> {
                assertEquals(expected.getId(), actual.id());
                assertEquals(expected.getTitle(), actual.title());
                assertEquals(expected.isPrivate(), actual.isPrivate());
                assertEquals(expected.getLanguage().getCode(), actual.lang());
            });
    }

    private SearchKitOptionsUseCase.Param createParam(Consumer<SearchKitOptionsUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private SearchKitOptionsUseCase.Param.ParamBuilder paramBuilder() {
        return SearchKitOptionsUseCase.Param.builder()
            .query("query")
            .page(0)
            .size(50)
            .currentUserId(UUID.randomUUID());
    }
}
