package org.flickit.assessment.scenario.test.users.space;

import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.exception.api.ErrorCodes.ACCESS_DENIED;
import static org.flickit.assessment.common.exception.api.ErrorCodes.INVALID_INPUT;
import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.scenario.fixture.request.CreateSpaceRequestDtoMother.createSpaceRequestDto;
import static org.flickit.assessment.scenario.fixture.request.UpdateSpaceRequestDtoMother.updateSpaceRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UpdateSpaceScenarioTest extends AbstractScenarioTest {

    @Autowired
    SpaceTestHelper spaceHelper;

    @Test
    void updateSpace() {
        var spaceId = createSpace();
        SpaceJpaEntity createdSpace = jpaTemplate.load(spaceId, SpaceJpaEntity.class);

        var updateRequest = updateSpaceRequestDto();
        spaceHelper.update(context, updateRequest, spaceId).then()
                .statusCode(200);

        SpaceJpaEntity updatedSpace = jpaTemplate.load(spaceId, SpaceJpaEntity.class);

        assertEquals(updateRequest.title(), updatedSpace.getTitle());
        assertEquals(generateSlugCode(updateRequest.title()), updatedSpace.getCode());
        assertEquals(getCurrentUserId(), updatedSpace.getLastModifiedBy());
        assertThat(updatedSpace.getLastModificationTime()).isAfter(createdSpace.getLastModificationTime());
    }

    @Test
    void updateSpace_withSameTitleForDifferentUsers() {
        // Create first space
        var firstSpaceId = createSpace();
        SpaceJpaEntity firstSpace = jpaTemplate.load(firstSpaceId, SpaceJpaEntity.class);

        // Change currentUser
        context.getNextCurrentUser();

        // Create second space for different user
        var secondSpaceId = createSpace();
        SpaceJpaEntity secondSpace = jpaTemplate.load(secondSpaceId, SpaceJpaEntity.class);

        // Update the second space with first space's title
        var updateRequest = updateSpaceRequestDto(b -> b.title(firstSpace.getTitle()));
        spaceHelper.update(context, updateRequest, secondSpaceId).then()
                .statusCode(200);

        SpaceJpaEntity updatedSecondSpace = jpaTemplate.load(secondSpaceId, SpaceJpaEntity.class);

        assertEquals(firstSpace.getTitle(), updatedSecondSpace.getTitle());
        assertEquals(firstSpace.getCode(), updatedSecondSpace.getCode());
        assertEquals(getCurrentUserId(), secondSpace.getLastModifiedBy());
        assertThat(updatedSecondSpace.getLastModificationTime()).isAfter(secondSpace.getLastModificationTime());
    }

    @Test
    void updateSpace_withSameTitleAsDeleted() {
        // Create first space
        var firstSpaceId = createSpace();
        SpaceJpaEntity firstSpace = jpaTemplate.load(firstSpaceId, SpaceJpaEntity.class);

        // Create second space with different title
        var secondSpaceId = createSpace();
        SpaceJpaEntity secondSpace = jpaTemplate.load(secondSpaceId, SpaceJpaEntity.class);

        // Delete the first space
        spaceHelper.delete(context, firstSpaceId);

        // Update the second space's title to match the deleted space's title
        var updateRequest = updateSpaceRequestDto(b -> b.title(firstSpace.getTitle()));
        spaceHelper.update(context, updateRequest, secondSpaceId).then()
                .statusCode(200);

        SpaceJpaEntity updatedSpace = jpaTemplate.load(secondSpaceId, SpaceJpaEntity.class);

        assertEquals(firstSpace.getTitle(), updatedSpace.getTitle());
        assertEquals(firstSpace.getCode(), updatedSpace.getCode());
        assertEquals(getCurrentUserId(), updatedSpace.getLastModifiedBy());
        assertThat(updatedSpace.getLastModificationTime()).isAfter(secondSpace.getLastModificationTime());
    }

    @Test
    void updateSpace_userIsNotOwner() {
        // Create space
        Number spaceId = createSpace();

        // Change currentUser which is not owner (creator) of the space
        context.getNextCurrentUser();

        var updateRequest = updateSpaceRequestDto();
        var error = spaceHelper.update(context, updateRequest, spaceId).then()
                .statusCode(403)
                .extract().as(ErrorResponseDto.class);

        SpaceJpaEntity space = jpaTemplate.load(spaceId, SpaceJpaEntity.class);

        assertEquals(space.getCreationTime(), space.getLastModificationTime());
        assertEquals(ACCESS_DENIED, error.code());
        assertNotNull(error.message());
    }

    @Test
    void updateSpace_withSameTitle() {
        // Create first space
        Number firstSpaceId = createSpace();
        SpaceJpaEntity firstSpace = jpaTemplate.load(firstSpaceId, SpaceJpaEntity.class);

        // Create second space with different title
        Number secondSpaceId = createSpace();
        SpaceJpaEntity secondSpace = jpaTemplate.load(secondSpaceId, SpaceJpaEntity.class);

        // Update the second space's title to match the first space's title
        var updateRequest = updateSpaceRequestDto(b -> b.title(firstSpace.getTitle()));
        var error = spaceHelper.update(context, updateRequest, secondSpaceId).then()
                .statusCode(400)
                .extract().as(ErrorResponseDto.class);

        SpaceJpaEntity updatedSpace = jpaTemplate.load(secondSpaceId, SpaceJpaEntity.class);

        assertEquals(secondSpace.getTitle(), updatedSpace.getTitle());
        assertEquals(updatedSpace.getCreationTime(), updatedSpace.getLastModificationTime());
        assertEquals(INVALID_INPUT, error.code());
        assertNotNull(error.message());
    }

    @Test
    void updateSpace_defaultSpace_title() {
        // Get the default space of the user
        var defaultSpace = loadSpaceByOwnerId(context.getCurrentUser().getUserId()).getFirst();

        // Update the default space's title
        var updateRequest = updateSpaceRequestDto(b -> b.title("new title"));
        var error = spaceHelper.update(context, updateRequest, defaultSpace.getId()).then()
            .statusCode(400)
            .extract().as(ErrorResponseDto.class);

        SpaceJpaEntity loadedSpace = jpaTemplate.load(defaultSpace.getId(), SpaceJpaEntity.class);

        assertEquals(defaultSpace.getTitle(), loadedSpace.getTitle());
        assertEquals(defaultSpace.getCreationTime(), loadedSpace.getLastModificationTime());
        assertEquals(INVALID_INPUT, error.code());
        assertNotNull(error.message());
    }

    private long createSpace() {
        var request = createSpaceRequestDto();
        var response = spaceHelper.create(context, request);
        Number id = response.path("id");
        return id.longValue();
    }

    private List<SpaceJpaEntity> loadSpaceByOwnerId(UUID ownerId) {
        return jpaTemplate.search(SpaceJpaEntity.class,
            (root, query, cb) -> cb.equal(root.get("ownerId"), ownerId));
    }
}
