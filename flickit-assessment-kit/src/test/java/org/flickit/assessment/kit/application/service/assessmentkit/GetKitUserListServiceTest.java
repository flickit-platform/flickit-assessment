package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitUserListUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitUsersPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.minio.CreateFileDownloadLinkPort;
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
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother.createExpertGroup;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetKitUserListServiceTest {

    @InjectMocks
    private GetKitUserListService service;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    private LoadKitUsersPort loadKitUsersPort;

    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    private final ExpertGroup expertGroup = createExpertGroup();

    @Test
    void testGetKitUserList_CurrentUserIsNotExpertGroupOwner_ThrowAccessDeniedException() {
        var param = createParam(GetKitUserListUseCase.Param.ParamBuilder::build);

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getKitUserList(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadKitUsersPort, createFileDownloadLinkPort);
    }

    @Test
    void testGetKitUserList_ValidInputs_ValidResults() {
        var param = createParam(b -> b.currentUserId(expertGroup.getOwnerId()));

        List<LoadKitUsersPort.KitUser> kitUserListItems = List.of(
            new LoadKitUsersPort.KitUser(UUID.randomUUID(), "UserName1", "UserEmail1@email.com", "filePath"),
            new LoadKitUsersPort.KitUser(expertGroup.getOwnerId(), "UserName2", "UserEmail2@email.com", null)
        );
        PaginatedResponse<LoadKitUsersPort.KitUser> paginatedResponse = new PaginatedResponse<>(
            kitUserListItems,
            param.getPage(),
            param.getSize(),
            UserJpaEntity.Fields.NAME,
            Sort.Direction.ASC.name().toLowerCase(),
            kitUserListItems.size());
        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(loadKitUsersPort.loadKitUsers(any(LoadKitUsersPort.Param.class))).thenReturn(paginatedResponse);
        String pictureLink = "cdn.flickit.org" + kitUserListItems.getFirst().picturePath();
        when(createFileDownloadLinkPort.createDownloadLink(any(String.class), any(Duration.class)))
            .thenReturn(pictureLink);

        var result = service.getKitUserList(param);

        ArgumentCaptor<LoadKitUsersPort.Param> loadPortParam = ArgumentCaptor.forClass(LoadKitUsersPort.Param.class);
        verify(loadKitUsersPort).loadKitUsers(loadPortParam.capture());
        assertEquals(param.getKitId(), loadPortParam.getValue().kitId());
        assertEquals(param.getPage(), loadPortParam.getValue().page());
        assertEquals(param.getSize(), loadPortParam.getValue().size());

        assertNotNull(result.getItems());
        assertEquals(2, result.getItems().size());
        assertEquals(param.getPage(), result.getPage());
        assertEquals(param.getSize(), result.getSize());
        assertEquals(kitUserListItems.getFirst().id(), result.getItems().getFirst().id());
        assertEquals(kitUserListItems.getFirst().email(), result.getItems().getFirst().email());
        assertEquals(kitUserListItems.getFirst().displayName(), result.getItems().getFirst().name());
        assertEquals(pictureLink, result.getItems().getFirst().pictureLink());
        assertFalse(result.getItems().getFirst().editable());

        assertEquals(kitUserListItems.get(1).id(), result.getItems().get(1).id());
        assertEquals(kitUserListItems.get(1).email(), result.getItems().get(1).email());
        assertEquals(kitUserListItems.get(1).displayName(), result.getItems().get(1).name());
        assertNull(result.getItems().get(1).pictureLink());
        assertTrue(result.getItems().get(1).editable());
    }

    @Test
    void testGetKitUserList_ValidInputs_EmptyResult() {
        var param = createParam(b -> b.currentUserId(expertGroup.getOwnerId()));

        PaginatedResponse<LoadKitUsersPort.KitUser> paginatedResponse = new PaginatedResponse<>(
            Collections.emptyList(),
            param.getPage(),
            param.getSize(),
            UserJpaEntity.Fields.NAME,
            Sort.Direction.ASC.name().toLowerCase(),
            0);
        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(loadKitUsersPort.loadKitUsers(any(LoadKitUsersPort.Param.class))).thenReturn(paginatedResponse);

        var result = service.getKitUserList(param);

        ArgumentCaptor<LoadKitUsersPort.Param> loadPortParam = ArgumentCaptor.forClass(LoadKitUsersPort.Param.class);
        verify(loadKitUsersPort).loadKitUsers(loadPortParam.capture());
        assertEquals(param.getKitId(), loadPortParam.getValue().kitId());
        assertEquals(param.getPage(), loadPortParam.getValue().page());
        assertEquals(param.getSize(), loadPortParam.getValue().size());

        assertNotNull(result.getItems());
        assertEquals(0, result.getItems().size());
        assertEquals(param.getPage(), result.getPage());
        assertEquals(param.getSize(), result.getSize());

        verifyNoInteractions(createFileDownloadLinkPort);
    }

    private GetKitUserListUseCase.Param createParam(Consumer<GetKitUserListUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetKitUserListUseCase.Param.ParamBuilder paramBuilder() {
        return GetKitUserListUseCase.Param.builder()
            .kitId(123L)
            .page(1)
            .size(10)
            .currentUserId(UUID.randomUUID());
    }
}
