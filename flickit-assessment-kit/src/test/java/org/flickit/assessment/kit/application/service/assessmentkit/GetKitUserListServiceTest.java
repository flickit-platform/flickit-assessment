package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitUserListUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitUsersPort;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetKitUserListServiceTest {

    @InjectMocks
    private GetKitUserListService service;

    @Mock
    private LoadKitUsersPort loadKitUsersPort;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Test
    void testGetKitUserList_ValidInputs_ValidResults() {
        Long kitId = 1L;
        int page = 0;
        int size = 10;
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();

        List<GetKitUserListUseCase.UserListItem> kitUserListItems = List.of(
            new GetKitUserListUseCase.UserListItem(UUID.randomUUID(), "UserName1", "UserEmail1@email.com"),
            new GetKitUserListUseCase.UserListItem(UUID.randomUUID(), "UserName2", "UserEmail2@email.com")
        );
        PaginatedResponse<GetKitUserListUseCase.UserListItem> paginatedResponse = new PaginatedResponse<>(
            kitUserListItems,
            page,
            size,
            UserJpaEntity.Fields.NAME,
            Sort.Direction.ASC.name().toLowerCase(),
            kitUserListItems.size());
        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        when(loadKitUsersPort.loadKitUsers(any(LoadKitUsersPort.Param.class))).thenReturn(paginatedResponse);

        var param = new GetKitUserListUseCase.Param(kitId, page, size, expertGroup.getOwnerId());
        var result = service.getKitUserList(param);

        var LoadExpertGroupIdParam = ArgumentCaptor.forClass(Long.class);
        verify(loadKitExpertGroupPort, times(1)).loadKitExpertGroup(LoadExpertGroupIdParam.capture());
        assertEquals(param.getKitId(), LoadExpertGroupIdParam.getValue());

        ArgumentCaptor<LoadKitUsersPort.Param> loadPortParam = ArgumentCaptor.forClass(LoadKitUsersPort.Param.class);
        verify(loadKitUsersPort).loadKitUsers(loadPortParam.capture());

        assertEquals(kitId, loadPortParam.getValue().kitId());
        assertEquals(page, loadPortParam.getValue().page());
        assertEquals(size, loadPortParam.getValue().size());
        assertNotNull(result.getItems());
        assertEquals(kitUserListItems, result.getItems());
        verify(loadKitUsersPort, times(1)).loadKitUsers(any(LoadKitUsersPort.Param.class));
    }

    @Test
    void testGetKitUserList_ValidInputs_EmptyResult() {
        Long kitId = 1L;
        int page = 0;
        int size = 10;
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();

        PaginatedResponse<GetKitUserListUseCase.UserListItem> paginatedResponse = new PaginatedResponse<>(
            Collections.emptyList(),
            page,
            size,
            UserJpaEntity.Fields.NAME,
            Sort.Direction.ASC.name().toLowerCase(),
            0);
        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        when(loadKitUsersPort.loadKitUsers(any(LoadKitUsersPort.Param.class))).thenReturn(paginatedResponse);

        var param = new GetKitUserListUseCase.Param(kitId, page, size, expertGroup.getOwnerId());
        var result = service.getKitUserList(param);

        var LoadExpertGroupIdParam = ArgumentCaptor.forClass(Long.class);
        verify(loadKitExpertGroupPort, times(1)).loadKitExpertGroup(LoadExpertGroupIdParam.capture());
        assertEquals(param.getKitId(), LoadExpertGroupIdParam.getValue());

        ArgumentCaptor<LoadKitUsersPort.Param> loadPortParam = ArgumentCaptor.forClass(LoadKitUsersPort.Param.class);
        verify(loadKitUsersPort).loadKitUsers(loadPortParam.capture());

        assertEquals(kitId, loadPortParam.getValue().kitId());
        assertEquals(page, loadPortParam.getValue().page());
        assertEquals(size, loadPortParam.getValue().size());
        assertNotNull(result.getItems());
        assertEquals(0, result.getItems().size());
        verify(loadKitUsersPort, times(1)).loadKitUsers(any(LoadKitUsersPort.Param.class));
    }
}
