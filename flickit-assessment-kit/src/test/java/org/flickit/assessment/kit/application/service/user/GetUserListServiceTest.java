package org.flickit.assessment.kit.application.service.user;

import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;
import org.flickit.assessment.kit.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.user.GetUserListUseCase;
import org.flickit.assessment.kit.application.port.out.user.LoadUsersByKitPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;

import static org.flickit.assessment.kit.test.fixture.application.UserMother.userListItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserListServiceTest {

    @InjectMocks
    private GetUserListService service;

    @Mock
    private LoadUsersByKitPort loadUsersByKitPort;

    @Test
    void testGetUserList_ValidInputs_ValidResults() {
        Long kitId = 1L;
        int page = 0;
        int size = 10;

        List<GetUserListUseCase.UserListItem> userListItems = List.of(
            userListItem("UserName1", "UserEmail1@email.com"),
            userListItem("UserName2", "UserEmail2@email.com")
        );
        PaginatedResponse<GetUserListUseCase.UserListItem> paginatedResponse = new PaginatedResponse<>(
            userListItems,
            page,
            size,
            UserJpaEntity.Fields.ID,
            Sort.Direction.ASC.name().toLowerCase(),
            userListItems.size());
        when(loadUsersByKitPort.load(any(LoadUsersByKitPort.Param.class))).thenReturn(paginatedResponse);

        var param = new GetUserListUseCase.Param(kitId, page, size);
        var result = service.getUserList(param);

        ArgumentCaptor<LoadUsersByKitPort.Param> loadPortParam = ArgumentCaptor.forClass(LoadUsersByKitPort.Param.class);
        verify(loadUsersByKitPort).load(loadPortParam.capture());

        assertEquals(kitId, loadPortParam.getValue().kitId());
        assertEquals(page, loadPortParam.getValue().page());
        assertEquals(size, loadPortParam.getValue().size());
        assertNotNull(result.getItems());
        assertEquals(userListItems, result.getItems());
        verify(loadUsersByKitPort, times(1)).load(any(LoadUsersByKitPort.Param.class));
    }

    @Test
    void testGetUserList_ValidInputs_EmptyResult() {
        Long kitId = 1L;
        int page = 0;
        int size = 10;

        PaginatedResponse<GetUserListUseCase.UserListItem> paginatedResponse = new PaginatedResponse<>(
            Collections.emptyList(),
            page,
            size,
            UserJpaEntity.Fields.ID,
            Sort.Direction.ASC.name().toLowerCase(),
            0);
        when(loadUsersByKitPort.load(any(LoadUsersByKitPort.Param.class))).thenReturn(paginatedResponse);

        var param = new GetUserListUseCase.Param(kitId, page, size);
        var result = service.getUserList(param);

        ArgumentCaptor<LoadUsersByKitPort.Param> loadPortParam = ArgumentCaptor.forClass(LoadUsersByKitPort.Param.class);
        verify(loadUsersByKitPort).load(loadPortParam.capture());

        assertEquals(kitId, loadPortParam.getValue().kitId());
        assertEquals(page, loadPortParam.getValue().page());
        assertEquals(size, loadPortParam.getValue().size());
        assertNotNull(result.getItems());
        assertEquals(0, result.getItems().size());
        verify(loadUsersByKitPort, times(1)).load(any(LoadUsersByKitPort.Param.class));
    }
}
