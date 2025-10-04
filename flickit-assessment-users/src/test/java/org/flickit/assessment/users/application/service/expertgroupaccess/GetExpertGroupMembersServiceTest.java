package org.flickit.assessment.users.application.service.expertgroupaccess;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.users.application.domain.ExpertGroup;
import org.flickit.assessment.users.application.domain.ExpertGroupAccessStatus;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.GetExpertGroupMembersUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.LoadExpertGroupMembersPort;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.flickit.assessment.users.application.domain.ExpertGroupAccessStatus.ACTIVE;
import static org.flickit.assessment.users.application.domain.ExpertGroupAccessStatus.PENDING;
import static org.flickit.assessment.users.test.fixture.application.ExpertGroupMother.createExpertGroup;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetExpertGroupMembersServiceTest {

    @InjectMocks
    private GetExpertGroupMembersService service;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private LoadExpertGroupMembersPort loadExpertGroupMembersPort;

    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    private final ExpertGroup expertGroup = createExpertGroup("picturePath", UUID.randomUUID());
    private final String expectedDownloadLink = "downloadLink";
    private final LoadExpertGroupMembersPort.Member member1 = createMember(UUID.randomUUID(), ExpertGroupAccessStatus.ACTIVE);
    private final LoadExpertGroupMembersPort.Member member2 = createMember(expertGroup.getOwnerId(), ExpertGroupAccessStatus.ACTIVE);

    @Test
    void testGetExpertGroupMembers_WhenCurrentUserIsOwnerAndNoStatusIsGiven_ThenReturnActiveMembers() {
        var param = createParam(b -> b.status(null).currentUserId(expertGroup.getOwnerId()));
        var paginatedResult = new PaginatedResponse<>(List.of(member1, member2),
            param.getPage(),
            param.getSize(),
            "title",
            "asc",
            2);

        when(loadExpertGroupOwnerPort.loadOwnerId(param.getId())).thenReturn(expertGroup.getOwnerId());
        when(loadExpertGroupMembersPort.loadExpertGroupMembers(param.getId(), ACTIVE, param.getPage(), param.getSize())).thenReturn(paginatedResult);
        when(createFileDownloadLinkPort.createDownloadLinkSafe(any(String.class), any(Duration.class))).thenReturn(expectedDownloadLink);

        var result = service.getExpertGroupMembers(param);

        assertNotNull(result.getItems());
        assertEquals(param.getPage(), result.getPage());
        assertEquals(param.getSize(), result.getSize());
        assertEquals(paginatedResult.getSort(), result.getSort());
        assertEquals(paginatedResult.getOrder(), result.getOrder());
        assertTrue(result.getItems().getFirst().deletable());
        assertFalse(result.getItems().getLast().deletable());
        assertEquals(paginatedResult.getTotal(), result.getTotal());

        assertThat(result.getItems())
            .zipSatisfy(paginatedResult.getItems(), (actual, expected) -> {
                assertEquals(expected.id(), actual.id());
                assertEquals(expected.email(), actual.email());
                assertEquals(expected.bio(), actual.bio());
                assertEquals(expected.displayName(), actual.displayName());
                assertEquals(expected.inviteExpirationDate(), actual.inviteExpirationDate());
                assertEquals(expected.status(), actual.status().ordinal());
                assertNotNull(actual.pictureLink());
            });

        verify(createFileDownloadLinkPort).createDownloadLinkSafe(member1.picture(), Duration.ofDays(1));
        verify(createFileDownloadLinkPort).createDownloadLinkSafe(member2.picture(), Duration.ofDays(1));
    }

    @Test
    void testGetExpertGroupMembers_WhenCurrentUserIsNotOwnerAndNoStatusIsGiven_ThenReturnActiveMembers() {
        var param = createParam(b -> b.status(null));
        var paginatedResult = new PaginatedResponse<>(List.of(member1, member2),
            param.getPage(),
            param.getSize(),
            "title",
            "asc",
            2);

        when(loadExpertGroupOwnerPort.loadOwnerId(param.getId())).thenReturn(expertGroup.getOwnerId());
        when(loadExpertGroupMembersPort.loadExpertGroupMembers(param.getId(), ACTIVE, param.getPage(), param.getSize())).thenReturn(paginatedResult);
        when(createFileDownloadLinkPort.createDownloadLinkSafe(any(String.class), any(Duration.class))).thenReturn(expectedDownloadLink);

        var result = service.getExpertGroupMembers(param);

        assertNotNull(result.getItems());
        assertEquals(param.getPage(), result.getPage());
        assertEquals(param.getSize(), result.getSize());
        assertEquals(paginatedResult.getSort(), result.getSort());
        assertEquals(paginatedResult.getOrder(), result.getOrder());
        assertFalse(result.getItems().getFirst().deletable());
        assertFalse(result.getItems().getLast().deletable());
        assertEquals(paginatedResult.getTotal(), result.getTotal());
        assertThat(result.getItems())
            .zipSatisfy(paginatedResult.getItems(), (actual, expected) -> {
                assertEquals(expected.id(), actual.id());
                assertEquals(expected.bio(), actual.bio());
                assertEquals(expected.displayName(), actual.displayName());
                assertEquals(expected.inviteExpirationDate(), actual.inviteExpirationDate());
                assertEquals(expected.status(), actual.status().ordinal());
                assertNull(actual.email());
                assertNotNull(actual.pictureLink());
            });

        verify(createFileDownloadLinkPort).createDownloadLinkSafe(member1.picture(), Duration.ofDays(1));
        verify(createFileDownloadLinkPort).createDownloadLinkSafe(member2.picture(), Duration.ofDays(1));
    }

    @Test
    void testGetExpertGroupMembers_WhenCurrentUserIsNotOwnerAndStatusIsPending_ThenReturnEmptyResult() {
        var param = createParam(b -> b.status(PENDING.name()));

        when(loadExpertGroupOwnerPort.loadOwnerId(param.getId())).thenReturn(expertGroup.getOwnerId());

        var result = service.getExpertGroupMembers(param);

        assertEquals(0, result.getItems().size());
        assertEquals(0, result.getPage());
        assertEquals(0, result.getSize());
        assertNull(result.getSort());
        assertNull(result.getOrder());
        assertEquals(0, result.getTotal());
    }

    @Test
    void testGetExpertGroupMembers_CurrentUserIsOwnerAndStatusIsPending_ThenReturnPendingMembers() {
        var param = createParam(b -> b.currentUserId(expertGroup.getOwnerId()).status(PENDING.name()));
        LoadExpertGroupMembersPort.Member pendingMember1 = createMember(UUID.randomUUID(), PENDING);
        LoadExpertGroupMembersPort.Member pendingMember2 = createMember(UUID.randomUUID(), PENDING);

        var paginatedResult = new PaginatedResponse<>(List.of(pendingMember1, pendingMember2),
            param.getPage(),
            param.getSize(),
            "title",
            "asc",
            2);

        when(loadExpertGroupOwnerPort.loadOwnerId(param.getId())).thenReturn(expertGroup.getOwnerId());
        when(loadExpertGroupMembersPort.loadExpertGroupMembers(param.getId(), PENDING, param.getPage(), param.getSize())).thenReturn(paginatedResult);
        when(createFileDownloadLinkPort.createDownloadLinkSafe(any(String.class), any(Duration.class))).thenReturn(expectedDownloadLink);

        var result = service.getExpertGroupMembers(param);

        assertNotNull(result.getItems());
        assertEquals(param.getPage(), result.getPage());
        assertEquals(param.getSize(), result.getSize());
        assertEquals(paginatedResult.getSort(), result.getSort());
        assertEquals(paginatedResult.getOrder(), result.getOrder());
        assertEquals(paginatedResult.getTotal(), result.getTotal());
        assertThat(result.getItems())
            .zipSatisfy(paginatedResult.getItems(), (actual, expected) -> {
                assertEquals(expected.id(), actual.id());
                assertEquals(expected.email(), actual.email());
                assertEquals(expected.bio(), actual.bio());
                assertEquals(expected.displayName(), actual.displayName());
                assertEquals(expected.inviteExpirationDate(), actual.inviteExpirationDate());
                assertEquals(expected.status(), actual.status().ordinal());
                assertNotNull(actual.pictureLink());
            });
        verify(createFileDownloadLinkPort).createDownloadLinkSafe(pendingMember1.picture(), Duration.ofDays(1));
        verify(createFileDownloadLinkPort).createDownloadLinkSafe(pendingMember2.picture(), Duration.ofDays(1));
    }

    private LoadExpertGroupMembersPort.Member createMember(UUID memberId, ExpertGroupAccessStatus status) {
        return new LoadExpertGroupMembersPort.Member(
            memberId,
            "email" + memberId + "@example.com",
            "Name" + memberId,
            "Bio" + memberId,
            "picture" + memberId + ".png",
            "http://www.example" + memberId + ".com",
            status.ordinal(),
            LocalDateTime.now());
    }

    private GetExpertGroupMembersUseCase.Param createParam(Consumer<GetExpertGroupMembersUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetExpertGroupMembersUseCase.Param.ParamBuilder paramBuilder() {
        return GetExpertGroupMembersUseCase.Param.builder()
            .expertGroupId(expertGroup.getId())
            .size(10)
            .page(0)
            .currentUserId(UUID.randomUUID())
            .status(ACTIVE.name());
    }
}
