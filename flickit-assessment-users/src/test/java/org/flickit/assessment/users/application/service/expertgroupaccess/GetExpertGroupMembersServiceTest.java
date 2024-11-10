package org.flickit.assessment.users.application.service.expertgroupaccess;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    private final String expectedDownloadLink = "downloadLink";
    private final UUID ownerId = UUID.randomUUID();
    private final int page = 0;
    private final int size = 10;
    private final LoadExpertGroupMembersPort.Member member1 = createPortResult(UUID.randomUUID());
    private final LoadExpertGroupMembersPort.Member member2 = createPortResult(ownerId);
    private final List<LoadExpertGroupMembersPort.Member> portMembers = List.of(member1, member2);
    private final List<GetExpertGroupMembersUseCase.Member> expectedMembers =
        List.of(portToUseCaseResult(member1, true), portToUseCaseResult(member2, false));

    @Test
    void testGetExpertGroupMembersService_WhenValidInputsAndNoStatusAndCurrentUserIsOwner_ShouldReturnValidResults() {
        var param = createParam(b -> b.status(null).currentUserId(ownerId));
        var paginatedResult = new PaginatedResponse<>(portMembers, param.getPage(), param.getSize(), "title", "asc", 2);

        when(loadExpertGroupOwnerPort.loadOwnerId(any(Long.class))).thenReturn(ownerId);
        when(loadExpertGroupMembersPort.loadExpertGroupMembers(param.getId(), 1, param.getPage(), param.getSize())).thenReturn(paginatedResult);
        when(createFileDownloadLinkPort.createDownloadLink(any(String.class), any(Duration.class))).thenReturn(expectedDownloadLink);

        var result = service.getExpertGroupMembers(param);
        assertEquals(expectedMembers, result.getItems());
        assertEquals(page, result.getPage());
        assertEquals(size, result.getSize());
        assertEquals("title", result.getSort());
        assertEquals("asc", result.getOrder());
        assertTrue(result.getItems().getFirst().deletable());
        assertFalse(result.getItems().getLast().deletable());
        assertEquals(portMembers.size(), result.getTotal());
    }

    @Test
    void testGetExpertGroupMembersService_WhenValidInputsAndNoStatusAndCurrentUserIsNotOwner_ShouldReturnValidResults() {
        var param = createParam(b -> b.status(null));
        var paginatedResult = new PaginatedResponse<>(portMembers, param.getPage(), param.getSize(), "title", "asc", 2);

        when(loadExpertGroupOwnerPort.loadOwnerId(any(Long.class))).thenReturn(ownerId);
        when(loadExpertGroupMembersPort.loadExpertGroupMembers(param.getId(), 1, param.getPage(), param.getSize())).thenReturn(paginatedResult);
        when(createFileDownloadLinkPort.createDownloadLink(any(String.class), any(Duration.class))).thenReturn(expectedDownloadLink);

        var result = service.getExpertGroupMembers(param);
        assertEquals(page, result.getPage());
        assertEquals(size, result.getSize());
        assertEquals("title", result.getSort());
        assertEquals("asc", result.getOrder());
        assertFalse(result.getItems().getFirst().deletable());
        assertFalse(result.getItems().getLast().deletable());
        assertEquals(portMembers.size(), result.getTotal());
    }

    @Test
    void testGetExpertGroupMembersService_WhenCurrentUserIsNotOwnerAndPendingStatus_ShouldReturnEmptyResult() {
        var param = createParam(b -> b.status(ExpertGroupAccessStatus.PENDING.name()));

        when(loadExpertGroupOwnerPort.loadOwnerId(any(Long.class))).thenReturn(ownerId);

        var result = service.getExpertGroupMembers(param);
        assertEquals(0, result.getItems().size());
        assertEquals(0, result.getPage());
        assertEquals(0, result.getSize());
        assertNull(result.getSort());
        assertNull(result.getOrder());
        assertEquals(0, result.getTotal());
    }

    @Test
    void testGetExpertGroupMembers_CurrentUserIsNotOwner_ResultWithoutEmail() {
        var param = createParam(GetExpertGroupMembersUseCase.Param.ParamBuilder::build);
        var paginatedResult = new PaginatedResponse<>(portMembers, param.getPage(), param.getSize(), "title", "asc", 2);

        when(loadExpertGroupOwnerPort.loadOwnerId(param.getId())).thenReturn(param.getCurrentUserId());
        when(loadExpertGroupMembersPort.loadExpertGroupMembers(param.getId(), 1, param.getPage(), param.getSize())).thenReturn(paginatedResult);
        when(createFileDownloadLinkPort.createDownloadLink(any(String.class), any(Duration.class))).thenReturn(expectedDownloadLink);

        var result = service.getExpertGroupMembers(param);
        assertNotNull(result.getItems());
        assertNotNull(result.getItems().stream().map(GetExpertGroupMembersUseCase.Member::email));
    }

    private LoadExpertGroupMembersPort.Member createPortResult(UUID memberId) {
        return new LoadExpertGroupMembersPort.Member(
            memberId,
            "email" + memberId + "@example.com",
            "Name" + memberId,
            "Bio" + memberId,
            "picture" + memberId + ".png",
            "http://www.example" + memberId + ".com",
            ExpertGroupAccessStatus.ACTIVE.ordinal(),
            LocalDateTime.now());
    }

    private GetExpertGroupMembersUseCase.Member portToUseCaseResult(LoadExpertGroupMembersPort.Member portMember, boolean deletable) {
        return new GetExpertGroupMembersUseCase.Member(
            portMember.id(),
            portMember.email(),
            portMember.displayName(),
            portMember.bio(),
            expectedDownloadLink,
            portMember.linkedin(),
            ExpertGroupAccessStatus.values()[portMember.status()],
            portMember.inviteExpirationDate(),
            deletable
        );
    }

    private GetExpertGroupMembersUseCase.Param createParam(Consumer<GetExpertGroupMembersUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetExpertGroupMembersUseCase.Param.ParamBuilder paramBuilder() {
        return GetExpertGroupMembersUseCase.Param.builder()
            .expertGroupId(123L)
            .size(size)
            .page(page)
            .currentUserId(UUID.randomUUID())
            .status(ExpertGroupAccessStatus.ACTIVE.name());
    }
}
