package org.flickit.assessment.kit.application.service.kittag;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.data.jpa.kit.kittag.KitTagJpaEntity;
import org.flickit.assessment.kit.application.domain.KitTag;
import org.flickit.assessment.kit.application.port.in.kittag.GetKitTagListUseCase;
import org.flickit.assessment.kit.application.port.out.kittag.LoadKitTagListPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;

import static org.flickit.assessment.kit.test.fixture.application.KitTagMother.createKitTag;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetKitTagListServiceTest {

    @InjectMocks
    private GetKitTagListService service;

    @Mock
    private LoadKitTagListPort loadKitTagListPort;

    @Test
    void testGetKitTagList_ValidInput_EmptyResult() {
        int page = 0;
        int size = 10;

        PaginatedResponse<KitTag> paginatedResponse = new PaginatedResponse<>(
            Collections.emptyList(),
            page,
            size,
            KitTagJpaEntity.Fields.CODE,
            Sort.Direction.ASC.name().toLowerCase(),
            0);
        when(loadKitTagListPort.loadAll(page, size)).thenReturn(paginatedResponse);

        PaginatedResponse<KitTag> result = service.getKitTagList(new GetKitTagListUseCase.Param(page, size));
        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void testGetKitTagList_ValidInput_ValidResult() {
        int page = 0;
        int size = 10;

        List<KitTag> kitTags = List.of(createKitTag("tag1"), createKitTag("tag2"), createKitTag("tag3"));

        PaginatedResponse<KitTag> paginatedResponse = new PaginatedResponse<>(
            kitTags,
            page,
            size,
            KitTagJpaEntity.Fields.CODE,
            Sort.Direction.ASC.name().toLowerCase(),
            0);
        when(loadKitTagListPort.loadAll(page, size)).thenReturn(paginatedResponse);

        PaginatedResponse<KitTag> result = service.getKitTagList(new GetKitTagListUseCase.Param(page, size));
        assertNotNull(result);
        assertEquals(kitTags.size(), result.getItems().size());
    }
}
