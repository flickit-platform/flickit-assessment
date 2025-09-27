package org.flickit.assessment.scenario.test.users.space;

import io.restassured.common.mapper.TypeRef;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaEntity;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.flickit.assessment.scenario.fixture.request.CreateAssessmentRequestDtoMother;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.flickit.assessment.scenario.test.core.assessment.AssessmentTestHelper;
import org.flickit.assessment.scenario.test.kit.assessmentkit.KitTestHelper;
import org.flickit.assessment.scenario.test.kit.kitdsl.KitDslTestHelper;
import org.flickit.assessment.scenario.test.kit.tag.KitTagTestHelper;
import org.flickit.assessment.scenario.test.users.expertgroup.ExpertGroupTestHelper;
import org.flickit.assessment.scenario.test.users.spaceuseraccess.SpaceUserAccessTestHelper;
import org.flickit.assessment.users.application.port.in.space.GetSpaceListUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.scenario.fixture.request.AddSpaceMemberRequestDtoMother.addSpaceMemberRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateExpertGroupRequestDtoMother.createExpertGroupRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateKitByDslRequestDtoMother.createKitByDslRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateSpaceRequestDtoMother.createSpaceRequestDto;
import static org.junit.jupiter.api.Assertions.*;

/**
 * System / scenario tests for GetSpaceList (covers mapping, filtering, ordering and pagination).
 */
public class GetSpaceListScenarioTest extends AbstractScenarioTest {

    @Autowired
    SpaceTestHelper spaceHelper;

    @Autowired
    SpaceUserAccessTestHelper spaceUserAccessHelper;

    @Autowired
    AssessmentTestHelper assessmentHelper;

    @Autowired
    KitTestHelper kitHelper;

    @Autowired
    KitDslTestHelper kitDslHelper;

    @Autowired
    KitTagTestHelper kitTagHelper;

    @Autowired
    ExpertGroupTestHelper expertGroupHelper;

    @Test
    void getSpaceList_basicMapping_andOwnerFlag_andCounts() {
        // create a space owned by current user
        var mySpaceTitle = "my-space";
        var mySpaceId = createBasicSpace(mySpaceTitle);

        // create another space and add current user as member (owner is different)
        UUID currentUserId = context.getCurrentUser().getUserId();
        UUID otherUserId = context.getNextCurrentUser().getUserId();
        var otherSpaceTitle = "other-space";
        var otherSpaceId = createSpaceByOtherUserAndAddUser(otherSpaceTitle, currentUserId);
        context.setCurrentUser(currentUserId);

        // create some assessments on both spaces
        var kitId = createKit();
        createAssessments(mySpaceId, kitId, 1);
        createAssessments(otherSpaceId, kitId, 2);

        // To assert that default space is not returned in result
        var userDefaultSpace = loadDefaultSpaceByUser(currentUserId);
        assertNotNull(userDefaultSpace);

        PaginatedResponse<GetSpaceListUseCase.SpaceListItem> paginatedResponse = spaceHelper.getUserSpaces(context, Map.of("page", 0, "size", 10))
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(new TypeRef<>() {
            });

        assertEquals(0, paginatedResponse.getPage());
        assertEquals(10, paginatedResponse.getSize());
        assertEquals(SpaceUserAccessJpaEntity.Fields.lastSeen, paginatedResponse.getSort());
        assertEquals(Sort.Direction.DESC.name().toLowerCase(), paginatedResponse.getOrder());
        assertEquals(2, paginatedResponse.getTotal());
        assertEquals(2, paginatedResponse.getItems().size());

        GetSpaceListUseCase.SpaceListItem firstItem = paginatedResponse.getItems().getFirst();
        GetSpaceListUseCase.SpaceListItem secondItem = paginatedResponse.getItems().getLast();
        assertNotNull(firstItem);
        assertNotNull(secondItem);

        assertEquals(otherSpaceId, firstItem.id());
        assertNotEquals(userDefaultSpace.getId(), firstItem.id());
        assertEquals(otherSpaceTitle, firstItem.title());
        assertEquals(otherUserId, firstItem.owner().id());
        assertFalse(firstItem.owner().isCurrentUserOwner());
        assertEquals(SpaceType.PREMIUM.getCode(), firstItem.type().code());
        assertTrue(firstItem.isActive());
        assertEquals(2, firstItem.membersCount());
        assertEquals(2, firstItem.assessmentsCount());

        assertEquals(mySpaceId, secondItem.id());
        assertNotEquals(userDefaultSpace.getId(), secondItem.id());
        assertEquals(mySpaceTitle, secondItem.title());
        assertEquals(currentUserId, secondItem.owner().id());
        assertTrue(secondItem.owner().isCurrentUserOwner());
        assertEquals(SpaceType.BASIC.getCode(), secondItem.type().code());
        assertTrue(secondItem.isActive());
        assertEquals(1, secondItem.membersCount());
        assertEquals(1, secondItem.assessmentsCount());
    }

    @Test
    void getSpaceList_orderedByLastSeen_and_pagination() {
        UUID currentUserId = context.getCurrentUser().getUserId();
        context.getNextCurrentUser();
        List<Long> createdSpaceIds = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            createdSpaceIds.add(createSpaceByOtherUserAndAddUser("space-order-" + i, currentUserId));
        spaceHelper.delete(context, createdSpaceIds.get(1));
        context.setCurrentUser(currentUserId);

        spaceUserAccessHelper.updateLastSeen(context, createdSpaceIds.getFirst());

        SpaceJpaEntity deletedSpace = jpaTemplate.load(createdSpaceIds.get(1), SpaceJpaEntity.class);
        assertTrue(deletedSpace.isDeleted());
        assertThat(deletedSpace.getDeletionTime()).isPositive();

        PaginatedResponse<GetSpaceListUseCase.SpaceListItem> firstPage = spaceHelper.getUserSpaces(context, Map.of("page", 0, "size", 3))
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(new TypeRef<>() {
            });

        PaginatedResponse<GetSpaceListUseCase.SpaceListItem> secondPage = spaceHelper.getUserSpaces(context, Map.of("page", 1, "size", 3))
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(new TypeRef<>() {
            });

        assertEquals(0, firstPage.getPage());
        assertEquals(3, firstPage.getSize());
        assertEquals(SpaceUserAccessJpaEntity.Fields.lastSeen, firstPage.getSort());
        assertEquals(Sort.Direction.DESC.name().toLowerCase(), firstPage.getOrder());
        assertEquals(4, firstPage.getTotal());
        assertEquals(3, firstPage.getItems().size());
        assertEquals(createdSpaceIds.getFirst(), firstPage.getItems().getFirst().id());
        assertEquals(createdSpaceIds.getLast(), firstPage.getItems().get(1).id());
        assertEquals(createdSpaceIds.get(3), firstPage.getItems().getLast().id());

        assertEquals(1, secondPage.getPage());
        assertEquals(3, secondPage.getSize());
        assertEquals(SpaceUserAccessJpaEntity.Fields.lastSeen, secondPage.getSort());
        assertEquals(Sort.Direction.DESC.name().toLowerCase(), secondPage.getOrder());
        assertEquals(4, secondPage.getTotal());
        assertEquals(1, secondPage.getItems().size());
        assertEquals(createdSpaceIds.get(2), secondPage.getItems().getFirst().id());
    }

    private Long createBasicSpace(String title) {
        var response = spaceHelper.create(context, createSpaceRequestDto(b -> b.title(title)));
        Number id = response.path("id");
        return id.longValue();
    }

    private Long createSpaceByOtherUserAndAddUser(String title, UUID userId) {
        var response = spaceHelper.create(context, createSpaceRequestDto(b -> b.title(title).type(SpaceType.PREMIUM.getCode())));
        Number id = response.path("id");
        long spaceId = id.longValue();

        var currentUserEmail = jpaTemplate.load(userId, UserJpaEntity.class).getEmail();

        var request = addSpaceMemberRequestDto(b -> b.email(currentUserEmail));
        spaceUserAccessHelper.create(context, spaceId, request).then().statusCode(200);

        return spaceId;
    }

    private void createAssessments(Long spaceId, Long kitId, int count) {
        for (int i = 0; i < count; i++) {
            var request = CreateAssessmentRequestDtoMother.createAssessmentRequestDto(a -> a
                .spaceId(spaceId)
                .assessmentKitId(kitId));
            var response = assessmentHelper.create(context, request);
            response.then().statusCode(201);
        }
    }

    private Long createKit() {
        Long expertGroupId = createExpertGroup();
        Long kitDslId = uploadDsl(expertGroupId);
        Long kitTagId = kitTagHelper.createKitTag();

        var request = createKitByDslRequestDto(a -> a
            .expertGroupId(expertGroupId)
            .kitDslId(kitDslId)
            .tagIds(List.of(kitTagId))
            .isPrivate(false)
        );

        var response = kitHelper.create(context, request);

        Number kitId = response.path("kitId");
        kitHelper.publishKit(context, kitId.longValue());
        return kitId.longValue();
    }

    private Long createExpertGroup() {
        var request = createExpertGroupRequestDto();
        var response = expertGroupHelper.create(context, request);
        Number id = response.path("id");
        return id.longValue();
    }

    private Long uploadDsl(Long expertGroupId) {
        var response = kitDslHelper.uploadDsl(context, "dummy-dsl.zip", "dsl.json", expertGroupId);
        Number id = response.path("kitDslId");
        return id.longValue();
    }

    private SpaceJpaEntity loadDefaultSpaceByUser(UUID currentUserId) {
        return jpaTemplate.findSingle(SpaceJpaEntity.class,
            (root, query, cb) -> {
                var subquery = query.subquery(Long.class);
                var accessRoot = subquery.from(SpaceUserAccessJpaEntity.class);
                subquery.select(accessRoot.get(SpaceUserAccessJpaEntity.Fields.spaceId))
                    .where(cb.equal(accessRoot.get(SpaceUserAccessJpaEntity.Fields.userId), currentUserId));

                return cb.and(
                    root.get(SpaceJpaEntity.Fields.id).in(subquery),
                    cb.isTrue(root.get(SpaceJpaEntity.Fields.isDefault)),
                    cb.isFalse(root.get(SpaceJpaEntity.Fields.deleted))
                );
            });
    }
}
