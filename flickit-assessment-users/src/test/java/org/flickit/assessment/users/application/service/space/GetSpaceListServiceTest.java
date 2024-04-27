package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaEntity;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.flickit.assessment.users.application.port.in.expertgroup.GetExpertGroupListUseCase;
import org.flickit.assessment.users.application.port.in.space.GetSpaceListUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupListPort;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceListPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSpaceListServiceTest {

    @InjectMocks
    private GetSpaceListService service;

    @Mock
    private LoadSpaceListPort loadSpaceListPort;

    @Test
    void testGetSpaceList_validInputs_validResults(){
        int size = 10;
        int page = 0;
        UUID currentUserId = UUID.randomUUID();
        var space1 = createSpace(currentUserId);
        var space2 = createSpace(currentUserId);
        var spacePortList = List.of(space1, space2);
        List<GetSpaceListUseCase.SpaceListItem> spaceListItems = List.of(
            portToUseCaseResult(space1, currentUserId),
            portToUseCaseResult(space2, currentUserId));

        PaginatedResponse<LoadSpaceListPort.Result> paginatedResponse = new PaginatedResponse<>(
            spacePortList,
            page,
            size,
            SpaceUserAccessJpaEntity.Fields.LAST_SEEN,
            Sort.Direction.DESC.name().toLowerCase(),
            spaceListItems.size());

        when(loadSpaceListPort.loadSpaceList(any(LoadSpaceListPort.Param.class))).thenReturn(paginatedResponse);

        GetSpaceListUseCase.Param param = new GetSpaceListUseCase.Param(size, page, currentUserId);
        var result = service.getSpaceList(param);

        ArgumentCaptor<LoadSpaceListPort.Param> loadPortParam = ArgumentCaptor.forClass(LoadSpaceListPort.Param.class);
        verify(loadSpaceListPort).loadSpaceList(loadPortParam.capture());

        assertEquals(page, loadPortParam.getValue().page());
        assertEquals(size, loadPortParam.getValue().size());
        assertNotNull(paginatedResponse);
        assertNotNull(result.getItems());
        assertNotEquals(0, result.getItems().size());
        assertEquals(spaceListItems, result.getItems());
    }

    private static long spaceId = 123;

    private static LoadSpaceListPort.Result createSpace(UUID ownerId) {
        long id = spaceId++;
        return new LoadSpaceListPort.Result(id,
            "title" + id,
            "Title" + id,
            ownerId,
            LocalDateTime.now(),
            1,
            2);
    }

    private static GetSpaceListUseCase.SpaceListItem portToUseCaseResult(LoadSpaceListPort.Result portResult, UUID currentUserId) {
        return new GetSpaceListUseCase.SpaceListItem(
            portResult.id(),
            portResult.code(),
            portResult.title(),
            portResult.ownerId().equals(currentUserId),
            portResult.lastModificationTime(),
            portResult.membersCount(),
            portResult.assessmentsCount()
        );
    }
}
