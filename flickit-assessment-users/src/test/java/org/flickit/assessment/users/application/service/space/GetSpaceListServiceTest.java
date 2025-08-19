package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaEntity;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.flickit.assessment.users.application.domain.SpaceStatus;
import org.flickit.assessment.users.application.port.in.space.GetSpaceListUseCase;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceListPort;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceListPort.Result;
import org.flickit.assessment.users.test.fixture.application.SpaceMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSpaceListServiceTest {

    @InjectMocks
    private GetSpaceListService service;

    @Mock
    private LoadSpaceListPort loadSpaceListPort;

    @Test
    void testGetSpaceList_validInputs_validResults() {
        var param = createParam(GetSpaceListUseCase.Param.ParamBuilder::build);
        var space1 = SpaceMother.basicSpace(param.getCurrentUserId());
        var space2 = SpaceMother.premiumSpace(UUID.randomUUID());
        var space3 = SpaceMother.inactiveSpace(UUID.randomUUID());
        var spacePortList = List.of(
            new LoadSpaceListPort.Result(space1, "owner1", 2, 5),
            new LoadSpaceListPort.Result(space2, "owner2", 4, 3),
            new LoadSpaceListPort.Result(space3, "owner3", 4, 6));

        PaginatedResponse<LoadSpaceListPort.Result> paginatedResponse = new PaginatedResponse<>(
            spacePortList,
            param.getPage(),
            param.getSize(),
            SpaceUserAccessJpaEntity.Fields.lastSeen,
            Sort.Direction.DESC.name().toLowerCase(),
            spacePortList.size());

        when(loadSpaceListPort.loadNonDefaultSpaceList(param.getCurrentUserId(), paginatedResponse.getPage(), param.getSize())).thenReturn(paginatedResponse);

        var result = service.getSpaceList(param);

        assertNotNull(paginatedResponse);
        assertNotNull(result.getItems());
        assertEquals(paginatedResponse.getItems().size(), result.getItems().size());
        assertEquals(paginatedResponse.getTotal(), result.getTotal());
        assertEquals(param.getSize(), result.getSize());
        assertEquals(param.getPage(), result.getPage());
        assertEquals(paginatedResponse.getSort(), result.getSort());
        assertEquals(paginatedResponse.getOrder(), result.getOrder());

        assertThat(result.getItems())
            .zipSatisfy(paginatedResponse.getItems(), (actual, expected) -> {
                assertEquals(expected.space().getId(), actual.id());
                assertEquals(expected.space().getTitle(), actual.title());
                assertEquals(expected.space().getOwnerId(), actual.owner().id());
                assertEquals(expected.ownerName(), actual.owner().displayName());
                assertEquals(SpaceStatus.ACTIVE.equals(expected.space().getStatus()), actual.isActive());
                assertEquals(expected.space().getLastModificationTime(), actual.lastModificationTime());
                assertEquals(expected.space().getType().getCode(), actual.type().code());
                assertEquals(expected.space().getType().getTitle(), actual.type().title());
                assertEquals(expected.assessmentsCount(), actual.assessmentsCount());
                assertEquals(expected.membersCount(), actual.membersCount());
            });
    }

    @Test
    void testGetSpaceList_ValidInputs_emptyResults() {
        var param = createParam(GetSpaceListUseCase.Param.ParamBuilder::build);

        PaginatedResponse<Result> paginatedResponse = new PaginatedResponse<>(
            Collections.emptyList(),
            param.getPage(),
            param.getSize(),
            UserJpaEntity.Fields.displayName,
            Sort.Direction.ASC.name().toLowerCase(),
            0);

        when(loadSpaceListPort.loadNonDefaultSpaceList(param.getCurrentUserId(), paginatedResponse.getPage(), param.getSize())).thenReturn(paginatedResponse);

        var result = service.getSpaceList(param);

        assertNotNull(paginatedResponse);
        assertNotNull(result.getItems());
        assertEquals(0, result.getItems().size());
        assertEquals(0, result.getTotal());
        assertEquals(param.getSize(), result.getSize());
        assertEquals(param.getPage(), result.getPage());
        assertEquals(paginatedResponse.getSort(), result.getSort());
        assertEquals(paginatedResponse.getOrder(), result.getOrder());
    }

    private GetSpaceListUseCase.Param createParam(Consumer<GetSpaceListUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetSpaceListUseCase.Param.ParamBuilder paramBuilder() {
        return GetSpaceListUseCase.Param.builder()
            .page(0)
            .size(10)
            .currentUserId(UUID.randomUUID());
    }
}
