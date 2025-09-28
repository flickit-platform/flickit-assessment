package org.flickit.assessment.scenario.test.users.expertgroup;

import io.restassured.common.mapper.TypeRef;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.data.jpa.users.expertgroupaccess.ExpertGroupAccessJpaEntity;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.flickit.assessment.scenario.test.kit.assessmentkit.KitTestHelper;
import org.flickit.assessment.scenario.test.kit.kitdsl.KitDslTestHelper;
import org.flickit.assessment.scenario.test.kit.tag.KitTagTestHelper;
import org.flickit.assessment.scenario.test.users.expertgroupaccess.ExpertGroupUserAccessTestHelper;
import org.flickit.assessment.scenario.test.users.user.UserTestHelper;
import org.flickit.assessment.users.application.domain.ExpertGroupAccessStatus;
import org.flickit.assessment.users.application.port.in.expertgroup.GetExpertGroupListUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.scenario.fixture.request.CreateExpertGroupRequestDtoMother.createExpertGroupRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateKitByDslRequestDtoMother.createKitByDslRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateUserRequestDtoMother.createUserRequestDto;
import static org.flickit.assessment.scenario.fixture.request.InviteExpertGroupMemberRequestDtoMother.inviteMemberRequestDto;
import static org.junit.jupiter.api.Assertions.*;

class GetExpertGroupListScenarioTest extends AbstractScenarioTest {

    @Autowired
    ExpertGroupTestHelper expertGroupHelper;

    @Autowired
    ExpertGroupUserAccessTestHelper expertGroupUserAccessHelper;

    @Autowired
    KitTestHelper kitHelper;

    @Autowired
    UserTestHelper userHelper;

    @Autowired
    KitDslTestHelper kitDslHelper;

    @Autowired
    KitTagTestHelper kitTagHelper;

    @Test
    void getExpertGroupList() {
        var firstGroupId = createExpertGroup();
        createKit(firstGroupId, true);

        UUID currentUserId = context.getCurrentUser().getUserId();
        context.getNextCurrentUser();
        var secondGroupId = createGroupByOtherUserAndAddUser(currentUserId);
        createKit(secondGroupId, false);
        createExpertGroup();
        context.setCurrentUser(currentUserId);
        confirmInvite(secondGroupId, currentUserId);

        PaginatedResponse<GetExpertGroupListUseCase.ExpertGroupListItem> response = expertGroupHelper.getList(context, Map.of("page", 0, "size", 10))
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(new TypeRef<>() {
            });

        assertPaginationParam(0, 10, 2, response);

        assertEquals(2, response.getItems().size());

        var firstItem = response.getItems().getFirst();
        var secondItem = response.getItems().getLast();

        var secondGroup = jpaTemplate.load(secondGroupId, ExpertGroupJpaEntity.class);
        assertEquals(secondGroup.getId(), firstItem.id());
        assertEquals(secondGroup.getTitle(), firstItem.title());
        assertEquals(secondGroup.getBio(), firstItem.bio());
        assertNotNull(firstItem.picture());
        assertEquals(0, firstItem.publishedKitsCount());
        assertEquals(2, firstItem.membersCount());
        assertFalse(firstItem.editable());
        assertMembers(secondGroupId, firstItem.members());

        var firstGroup = jpaTemplate.load(firstGroupId, ExpertGroupJpaEntity.class);
        assertEquals(firstGroup.getId(), secondItem.id());
        assertEquals(firstGroup.getTitle(), secondItem.title());
        assertEquals(firstGroup.getBio(), secondItem.bio());
        assertNotNull(secondItem.picture());
        assertEquals(1, secondItem.publishedKitsCount());
        assertEquals(1, secondItem.membersCount());
        assertTrue(secondItem.editable());
        assertMembers(firstGroupId, secondItem.members());
    }

    @Test
    void getExpertGroupList_orderedByLastSeen_and_pagination() {
        UUID currentUserId = context.getCurrentUser().getUserId();
        List<Long> createdGroupIds = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            createdGroupIds.add(createGroupByOtherUserAndAddUser(currentUserId));

        expertGroupHelper.delete(context, createdGroupIds.get(1));

        expertGroupUserAccessHelper.updateLastSeen(context, createdGroupIds.getFirst());

        PaginatedResponse<GetExpertGroupListUseCase.ExpertGroupListItem> firstPage = expertGroupHelper.getList(context, Map.of("page", 0, "size", 3))
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(new TypeRef<>() {
            });

        PaginatedResponse<GetExpertGroupListUseCase.ExpertGroupListItem> secondPage = expertGroupHelper.getList(context, Map.of("page", 1, "size", 3))
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(new TypeRef<>() {
            });

        assertPaginationParam(0, 3, 4, firstPage);
        assertEquals(3, firstPage.getItems().size());
        assertEquals(createdGroupIds.getFirst(), firstPage.getItems().getFirst().id());
        assertEquals(createdGroupIds.getLast(), firstPage.getItems().get(1).id());
        assertEquals(createdGroupIds.get(3), firstPage.getItems().getLast().id());

        assertPaginationParam(1, 3, 4, secondPage);
        assertEquals(1, secondPage.getItems().size());
        assertEquals(createdGroupIds.get(2), secondPage.getItems().getFirst().id());
    }

    @Test
    void getExpertGroupList_membersCappedAtFive() {
        long groupId = createExpertGroup();
        UUID ownerId = context.getCurrentUser().getUserId();

        for (int i = 0; i < 7; i++) {
            var inviteeId = UUID.randomUUID();
            userHelper.create(createUserRequestDto(b -> b.id(inviteeId)));
            var request = inviteMemberRequestDto(b -> b.userId(inviteeId));
            expertGroupUserAccessHelper.invite(context, groupId, request);
            context.setCurrentUser(inviteeId);
            confirmInvite(groupId, inviteeId);

            context.setCurrentUser(ownerId);
        }

        var response = expertGroupHelper.getList(context, Map.of("page", 0, "size", 10))
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(new TypeRef<PaginatedResponse<GetExpertGroupListUseCase.ExpertGroupListItem>>() {
            });

        assertPaginationParam(0, 10, 1, response);

        var groupItem = response.getItems().getFirst();
        assertEquals(8, groupItem.membersCount()); // owner + 7
        assertTrue(groupItem.members().size() <= 5);
        assertMembers(groupId, groupItem.members());
    }

    @Test
    void getExpertGroupList_emptyWhenNoGroups() {
        var response = expertGroupHelper.getList(context, Map.of("page", 0, "size", 10))
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(new TypeRef<PaginatedResponse<GetExpertGroupListUseCase.ExpertGroupListItem>>() {
            });

        assertPaginationParam(0, 10, 0, response);
        assertTrue(response.getItems().isEmpty());
    }

    @Test
    void getExpertGroupList_pendingInviteNotListed() {
        UUID currentUserId = context.getCurrentUser().getUserId();
        context.getNextCurrentUser();

        var groupId = createExpertGroup();
        var request = inviteMemberRequestDto(b -> b.userId(currentUserId));
        expertGroupUserAccessHelper.invite(context, groupId, request).then().statusCode(201);

        context.setCurrentUser(currentUserId);

        var response = expertGroupHelper.getList(context, Map.of("page", 0, "size", 10))
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(new TypeRef<PaginatedResponse<GetExpertGroupListUseCase.ExpertGroupListItem>>() {
            });

        assertTrue(response.getItems().stream().noneMatch(i -> i.id().equals(groupId)));
    }

    private void assertPaginationParam(int page, int size, int total, PaginatedResponse<GetExpertGroupListUseCase.ExpertGroupListItem> pageResponse) {
        assertEquals(page, pageResponse.getPage());
        assertEquals(size, pageResponse.getSize());
        assertEquals(ExpertGroupAccessJpaEntity.Fields.lastSeen, pageResponse.getSort());
        assertEquals(Sort.Direction.DESC.name().toLowerCase(), pageResponse.getOrder());
        assertEquals(total, pageResponse.getTotal());
    }

    private void assertMembers(long expertGroupId, List<GetExpertGroupListUseCase.Member> members) {
        var actual = members.stream().map(GetExpertGroupListUseCase.Member::displayName).toList();
        var allMembers = findMembers(expertGroupId);
        var expected = allMembers.stream()
            .sorted()
            .limit(5)
            .toList();

        assertEquals(expected, actual);
    }

    private Long createExpertGroup() {
        var request = createExpertGroupRequestDto();
        var response = expertGroupHelper.create(context, request);
        Number id = response.path("id");
        return id.longValue();
    }

    private Long createGroupByOtherUserAndAddUser(UUID userId) {
        var response = expertGroupHelper.create(context, createExpertGroupRequestDto());
        Number id = response.path("id");
        long groupId = id.longValue();

        var request = inviteMemberRequestDto(b -> b.userId(userId));
        expertGroupUserAccessHelper.invite(context, groupId, request);

        return groupId;
    }

    private void createKit(Long groupId, boolean publish) {
        Long kitDslId = uploadDsl(groupId);
        Long kitTagId = kitTagHelper.createKitTag();

        var request = createKitByDslRequestDto(a -> a
            .expertGroupId(groupId)
            .kitDslId(kitDslId)
            .tagIds(List.of(kitTagId))
            .isPrivate(false)
        );

        var response = kitHelper.create(context, request);

        Number kitId = response.path("kitId");
        if (publish)
            kitHelper.publishKit(context, kitId.longValue());
    }

    private Long uploadDsl(Long groupId) {
        var response = kitDslHelper.uploadDsl(context, "dummy-dsl.zip", "dsl.json", groupId);
        Number id = response.path("kitDslId");
        return id.longValue();
    }

    private void confirmInvite(long expertGroupId, UUID userId) {
        var token = jpaTemplate.findSingle(ExpertGroupAccessJpaEntity.class,
            (root, query, cb) ->
                cb.and(
                    cb.equal(root.get(ExpertGroupAccessJpaEntity.Fields.expertGroupId), expertGroupId),
                    cb.equal(root.get(ExpertGroupAccessJpaEntity.Fields.userId), userId))
        ).getInviteToken().toString();

        expertGroupUserAccessHelper.confirm(context, expertGroupId, token).then().statusCode(200);
    }

    private List<String> findMembers(long expertGroupId) {
        return jpaTemplate.search(UserJpaEntity.class,
                (root, query, cb) -> {
                    var subquery = query.subquery(UUID.class);
                    var accessRoot = subquery.from(ExpertGroupAccessJpaEntity.class);
                    subquery.select(accessRoot.get(ExpertGroupAccessJpaEntity.Fields.userId))
                        .where(cb.equal(accessRoot.get(ExpertGroupAccessJpaEntity.Fields.expertGroupId), expertGroupId),
                            cb.equal(accessRoot.get(ExpertGroupAccessJpaEntity.Fields.status), ExpertGroupAccessStatus.ACTIVE.ordinal()));

                    return cb.and(
                        root.get(UserJpaEntity.Fields.id).in(subquery)
                    );
                }).stream()
            .map(UserJpaEntity::getDisplayName)
            .toList();
    }
}
