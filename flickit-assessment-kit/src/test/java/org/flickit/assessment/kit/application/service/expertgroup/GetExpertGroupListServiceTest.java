package org.flickit.assessment.kit.application.service.expertgroup;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupListUseCase;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupListUseCase.Member;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupListPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupListPort.Result;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupPictureLinkPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetExpertGroupListServiceTest {

    @InjectMocks
    private GetExpertGroupListService service;
    @Mock
    private LoadExpertGroupListPort loadExpertGroupListPort;
    @Mock
    private LoadExpertGroupPictureLinkPort loadExpertGroupPictureLinkPort;

    @Test
    void testGetExpertGroupList_ValidInputs_ValidResults() {
        int page = 0;
        int size = 10;
        UUID currentUserId = UUID.randomUUID();

        var expertGroup1 = createExpertGroup(UUID.randomUUID());
        var expertGroup2 = createExpertGroup(currentUserId);
        List<Result> expertGroups = List.of(expertGroup1, expertGroup2);

        PaginatedResponse<Result> paginatedResponse = new PaginatedResponse<>(
            expertGroups,
            page,
            size,
            UserJpaEntity.Fields.NAME,
            Sort.Direction.ASC.name().toLowerCase(),
            expertGroups.size());
        when(loadExpertGroupListPort.loadExpertGroupList(any(LoadExpertGroupListPort.Param.class)))
            .thenReturn(paginatedResponse);

        when(loadExpertGroupPictureLinkPort.loadPictureLink(any(String.class),any(Duration.class)))
            .thenReturn(any(String.class));

        var param = new GetExpertGroupListUseCase.Param(size, page, currentUserId);
        var result = service.getExpertGroupList(param);

        ArgumentCaptor<LoadExpertGroupListPort.Param> loadPortParam = ArgumentCaptor.forClass(LoadExpertGroupListPort.Param.class);
        verify(loadExpertGroupListPort).loadExpertGroupList(loadPortParam.capture());

        assertEquals(page, loadPortParam.getValue().page());
        assertEquals(size, loadPortParam.getValue().size());
        assertNotNull(paginatedResponse);
        assertNotNull(result.getItems());
        assertEquals(2,result.getItems().size());
        assertNotNull(result.getItems());
    }

    @Test
    void testGetExpertGroupList_ValidInputs_emptyResults() {
        int page = 0;
        int size = 10;
        UUID currentUserId = UUID.randomUUID();

        List<Result> expertGroupListItems = Collections.emptyList();

        List<GetExpertGroupListUseCase.ExpertGroupListItem> expertGroupListItemsFinal = Collections.emptyList();

        PaginatedResponse<Result> paginatedResponse = new PaginatedResponse<>(
            expertGroupListItems,
            page,
            size,
            UserJpaEntity.Fields.NAME,
            Sort.Direction.ASC.name().toLowerCase(),
            0);
        when(loadExpertGroupListPort.loadExpertGroupList(any(LoadExpertGroupListPort.Param.class)))
            .thenReturn(paginatedResponse);

        var param = new GetExpertGroupListUseCase.Param(size, page, currentUserId);
        var result = service.getExpertGroupList(param);

        ArgumentCaptor<LoadExpertGroupListPort.Param> loadPortParam = ArgumentCaptor.forClass(LoadExpertGroupListPort.Param.class);
        verify(loadExpertGroupListPort).loadExpertGroupList(loadPortParam.capture());

        assertEquals(page, loadPortParam.getValue().page());
        assertEquals(size, loadPortParam.getValue().size());
        assertNotNull(paginatedResponse);
        assertNotNull(result.getItems());
        assertEquals(expertGroupListItemsFinal, result.getItems());
    }

    private static long expertGroupId = 123;

    private static Result createExpertGroup(UUID ownerId) {
        long id = expertGroupId++;
        return new Result(id,
            "Title" + id,
            "Bio" + id,
            "Picture" + id,
            2,
            10,
            List.of(new Member("title" + id)),
            ownerId);
    }
}

