package org.flickit.assessment.kit.application.service.expertgroup;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupListUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupListPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetExpertGroupListServiceTest {

    @InjectMocks
    private GetExpertGroupListService service;

    @Mock
    private LoadExpertGroupListPort loadExpertGroupListPort;

    @Test
    void testGetExpertGroupList_ValidInputs_ValidResults() {
        int page = 0;
        int size = 10;
        UUID currentUserId = UUID.randomUUID();
        long expertGroupId1 = new Random().nextLong();
        long expertGroupId2 = new Random().nextLong();

        List<GetExpertGroupListUseCase.Member> members = new LinkedList<>();
        members.add(new GetExpertGroupListUseCase.Member("Member 1"));
        members.add(new GetExpertGroupListUseCase.Member("Member 2"));

        List<GetExpertGroupListUseCase.ExpertGroupListItem> expertGroupListItems = List.of(
            new GetExpertGroupListUseCase.ExpertGroupListItem(expertGroupId1, "ExpertGroup title 1",
                "ExpertGroup bio 1", "ExpertGroup picture 1", 3, 2,
                members,UUID.randomUUID(),null),
            new GetExpertGroupListUseCase.ExpertGroupListItem(expertGroupId2, "ExpertGroup title 2",
                "ExpertGroup bio 2", "ExpertGroup picture 2", 3, 2,
                members,currentUserId,null)
        );

        List<GetExpertGroupListUseCase.ExpertGroupListItemFinal> expertGroupListItemsFinal = List.of(
            new GetExpertGroupListUseCase.ExpertGroupListItemFinal(expertGroupId1, "ExpertGroup title 1",
                "ExpertGroup bio 1", "ExpertGroup picture 1", 3, 2,
                members,false),
            new GetExpertGroupListUseCase.ExpertGroupListItemFinal(expertGroupId2, "ExpertGroup title 2",
                "ExpertGroup bio 2", "ExpertGroup picture 2", 3, 2,
                members,true)
        );

        PaginatedResponse<GetExpertGroupListUseCase.ExpertGroupListItem> paginatedResponse = new PaginatedResponse<>(
            expertGroupListItems,
            page,
            size,
            UserJpaEntity.Fields.NAME,
            Sort.Direction.ASC.name().toLowerCase(),
            expertGroupListItems.size());
        when(loadExpertGroupListPort.loadExpertGroupList(any (LoadExpertGroupListPort.Param.class)))
            .thenReturn(paginatedResponse);

        var param = new GetExpertGroupListUseCase.Param(size, page, currentUserId);
        var result = service.getExpertGroupList(param);

        ArgumentCaptor<LoadExpertGroupListPort.Param> loadPortParam = ArgumentCaptor.forClass(LoadExpertGroupListPort.Param.class);
        verify(loadExpertGroupListPort).loadExpertGroupList(loadPortParam.capture());

        assertEquals(page, loadPortParam.getValue().page());
        assertEquals(size, loadPortParam.getValue().size());
        assertNotNull(paginatedResponse);
        assertNotNull(result.getItems());
        assertNotEquals(0, result.getItems().size());
        assertEquals(expertGroupListItemsFinal,result.getItems());
    }

    @Test
    void testGetExpertGroupList_ValidInputs_emptyResults() {
        int page = 0;
        int size = 10;
        UUID currentUserId = UUID.randomUUID();
        

        List<GetExpertGroupListUseCase.ExpertGroupListItem> expertGroupListItems = Collections.emptyList();

        List<GetExpertGroupListUseCase.ExpertGroupListItemFinal> expertGroupListItemsFinal = Collections.emptyList();

        PaginatedResponse<GetExpertGroupListUseCase.ExpertGroupListItem> paginatedResponse = new PaginatedResponse<>(
            expertGroupListItems,
            page,
            size,
            UserJpaEntity.Fields.NAME,
            Sort.Direction.ASC.name().toLowerCase(),
            expertGroupListItems.size());
        when(loadExpertGroupListPort.loadExpertGroupList(any (LoadExpertGroupListPort.Param.class)))
            .thenReturn(paginatedResponse);

        var param = new GetExpertGroupListUseCase.Param(size, page, currentUserId);
        var result = service.getExpertGroupList(param);

        ArgumentCaptor<LoadExpertGroupListPort.Param> loadPortParam = ArgumentCaptor.forClass(LoadExpertGroupListPort.Param.class);
        verify(loadExpertGroupListPort).loadExpertGroupList(loadPortParam.capture());

        assertEquals(page, loadPortParam.getValue().page());
        assertEquals(size, loadPortParam.getValue().size());
        assertNotNull(paginatedResponse);
        assertNotNull(result.getItems());
        assertEquals(0, result.getItems().size());
        assertEquals(expertGroupListItemsFinal,result.getItems());
    }

}

