package org.flickit.assessment.users.application.service.expertgroup;

import org.flickit.assessment.common.application.domain.crud.Order;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.flickit.assessment.users.application.port.in.expertgroup.GetExpertGroupListUseCase;
import org.flickit.assessment.users.application.port.in.expertgroup.GetExpertGroupListUseCase.Member;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupListPort;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupListPort.Result;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
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
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    private static long expertGroupId = 123;
    private final int page = 0;
    private final int size = 10;
    private final UUID currentUserId = UUID.randomUUID();
    private final GetExpertGroupListUseCase.Param param = new GetExpertGroupListUseCase.Param(size, page, currentUserId);

    @Test
    void testGetExpertGroupList_ValidInputs_ValidResults() {
        var expertGroup1 = createExpertGroup(UUID.randomUUID());
        var expertGroup2 = createExpertGroup(currentUserId);
        List<Result> expertGroups = List.of(expertGroup1, expertGroup2);

        List<GetExpertGroupListUseCase.ExpertGroupListItem> expertGroupListItems = List.of(
            portToUseCaseResult(expertGroup1, false),
            portToUseCaseResult(expertGroup2, true));

        PaginatedResponse<Result> paginatedResponse = new PaginatedResponse<>(
            expertGroups,
            page,
            size,
            UserJpaEntity.Fields.displayName,
            Sort.Direction.ASC.name().toLowerCase(),
            expertGroups.size());

        when(loadExpertGroupListPort.loadExpertGroupList(any(LoadExpertGroupListPort.Param.class)))
            .thenReturn(paginatedResponse);
        when(createFileDownloadLinkPort.createDownloadLink(expertGroup1.picture(), Duration.ofDays(1)))
            .thenReturn(expertGroup1.picture());
        when(createFileDownloadLinkPort.createDownloadLink(expertGroup2.picture(), Duration.ofDays(1)))
            .thenReturn(expertGroup2.picture());

        var result = service.getExpertGroupList(param);

        ArgumentCaptor<LoadExpertGroupListPort.Param> loadPortParam = ArgumentCaptor.forClass(LoadExpertGroupListPort.Param.class);
        verify(loadExpertGroupListPort).loadExpertGroupList(loadPortParam.capture());

        assertEquals(page, loadPortParam.getValue().page());
        assertEquals(size, loadPortParam.getValue().size());
        assertEquals(expertGroupListItems, result.getItems());

        assertPaginationProperties(expertGroups, result);
    }

    @Test
    void testGetExpertGroupList_ValidInputs_emptyResults() {
        List<Result> expertGroupListItems = Collections.emptyList();
        PaginatedResponse<Result> paginatedResponse = new PaginatedResponse<>(
            expertGroupListItems,
            page,
            size,
            UserJpaEntity.Fields.displayName,
            Sort.Direction.ASC.name().toLowerCase(),
            0);

        when(loadExpertGroupListPort.loadExpertGroupList(any(LoadExpertGroupListPort.Param.class)))
            .thenReturn(paginatedResponse);

        var result = service.getExpertGroupList(param);

        ArgumentCaptor<LoadExpertGroupListPort.Param> loadPortParam = ArgumentCaptor.forClass(LoadExpertGroupListPort.Param.class);
        verify(loadExpertGroupListPort).loadExpertGroupList(loadPortParam.capture());

        assertEquals(page, loadPortParam.getValue().page());
        assertEquals(size, loadPortParam.getValue().size());
        assertEquals(Collections.emptyList(), result.getItems());

        assertPaginationProperties(expertGroupListItems, result);
    }

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

    private static GetExpertGroupListUseCase.ExpertGroupListItem portToUseCaseResult(Result portResult, boolean editable) {
        return new GetExpertGroupListUseCase.ExpertGroupListItem(
            portResult.id(),
            portResult.title(),
            portResult.bio(),
            portResult.picture(),
            portResult.publishedKitsCount(),
            portResult.membersCount(),
            portResult.members(),
            editable
        );
    }

    private void assertPaginationProperties(List<Result> expertGroups, PaginatedResponse<GetExpertGroupListUseCase.ExpertGroupListItem> paginatedResponse) {
        assertEquals(expertGroups.size(), paginatedResponse.getTotal());
        assertEquals(param.getSize(), paginatedResponse.getSize());
        assertEquals(param.getPage(), paginatedResponse.getPage());
        assertEquals(UserJpaEntity.Fields.displayName, paginatedResponse.getSort());
        assertEquals(Order.ASC.getTitle(), paginatedResponse.getOrder());
    }
}

