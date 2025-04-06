package org.flickit.assessment.scenario.test.users.space;

import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.flickit.assessment.common.exception.api.ErrorCodes.ACCESS_DENIED;
import static org.flickit.assessment.common.exception.api.ErrorCodes.INVALID_INPUT;
import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.scenario.fixture.request.CreateSpaceRequestDtoMother.createSpaceRequestDto;
import static org.flickit.assessment.scenario.fixture.request.UpdateSpaceRequestDtoMother.updateSpaceRequestDto;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

class UpdateSpaceScenarioTest extends AbstractScenarioTest {

    @Autowired
    SpaceTestHelper spaceHelper;

    @Test
    void updateSpace() {
        var createRequest = createSpaceRequestDto();
        var createResponse = spaceHelper.create(context, createRequest);

        createResponse.then()
            .statusCode(201)
            .body("id", notNullValue());

        Number spaceId = createResponse.body().path("id");
        SpaceJpaEntity createdSpace = jpaTemplate.load(spaceId, SpaceJpaEntity.class);

        var newTitle = "new title";
        var updateRequest = updateSpaceRequestDto(b -> b.title(newTitle));
        spaceHelper.update(context, updateRequest, spaceId).then()
            .statusCode(200);

        SpaceJpaEntity updatedSpace = jpaTemplate.load(spaceId, SpaceJpaEntity.class);

        assertEquals(newTitle, updatedSpace.getTitle());
        assertEquals(generateSlugCode(newTitle), updatedSpace.getCode());
        assertEquals(context.getCurrentUser().getUserId(), updatedSpace.getLastModifiedBy());
        assertTrue(updatedSpace.getLastModificationTime().isAfter(createdSpace.getLastModificationTime()));
    }

    @Test
    void updateSpace_withSameTitleForDifferentUsers() {
        var firstCreateRequest = createSpaceRequestDto();
        var firstCreateResponse = spaceHelper.create(context, firstCreateRequest);
        // Create first space
        firstCreateResponse.then()
            .statusCode(201);
        // Change currentUser
        context.getNextCurrentUser();
        var secondCreateRequest = createSpaceRequestDto();
        // Create second space for different user
        var secondCreateResponse = spaceHelper.create(context, secondCreateRequest);
        secondCreateResponse.then()
            .statusCode(201)
            .body("id", notNullValue());

        Number secondSpaceId = secondCreateResponse.body().path("id");
        SpaceJpaEntity secondSpace = jpaTemplate.load(secondSpaceId, SpaceJpaEntity.class);
        // Update the second space with first space's title
        var updateRequest = updateSpaceRequestDto(b -> b.title(firstCreateRequest.title()));
        spaceHelper.update(context, updateRequest, secondSpaceId).then()
            .statusCode(200);

        SpaceJpaEntity updatedSecondSpace = jpaTemplate.load(secondSpaceId, SpaceJpaEntity.class);

        assertEquals(firstCreateRequest.title(), updatedSecondSpace.getTitle());
        assertEquals(generateSlugCode(updatedSecondSpace.getTitle()), updatedSecondSpace.getCode());
        assertEquals(context.getCurrentUser().getUserId(), secondSpace.getLastModifiedBy());
        assertTrue(updatedSecondSpace.getLastModificationTime().isAfter(secondSpace.getLastModificationTime()));
    }

    @Test
    void updateSpace_userIsNotOwner() {
        var createRequest = createSpaceRequestDto();
        var createResponse = spaceHelper.create(context, createRequest);

        createResponse.then()
            .statusCode(201)
            .body("id", notNullValue());

        Number spaceId = createResponse.body().path("id");

        var updateRequest = updateSpaceRequestDto(b -> b.title("newTitle"));
        // Change currentUser which is not owner (creator) of the space
        context.getNextCurrentUser();
        var error = spaceHelper.update(context, updateRequest, spaceId).then()
            .statusCode(403)
            .extract().as(ErrorResponseDto.class);

        SpaceJpaEntity space = jpaTemplate.load(spaceId, SpaceJpaEntity.class);

        assertEquals(ACCESS_DENIED, error.code());
        assertEquals(createRequest.title(), space.getTitle());
        assertEquals(space.getCreationTime(), space.getLastModificationTime());
        assertNotNull(error.message());
    }

    @Test
    void updateSpace_withSameTitle() {
        var firstCreateRequest = createSpaceRequestDto();
        // Create first space
        var firstCreateResponse = spaceHelper.create(context, firstCreateRequest);
        firstCreateResponse.then()
            .statusCode(201)
            .body("id", notNullValue());

        var secondCreateRequest = createSpaceRequestDto();
        // Create second space with different request
        var secondCreateResponse = spaceHelper.create(context, secondCreateRequest);
        secondCreateResponse.then()
            .statusCode(201)
            .body("id", notNullValue());

        Number secondSpaceId = secondCreateResponse.body().path("id");
        // Update the second space's title to match the first space's title
        var updateRequest = updateSpaceRequestDto(b -> b.title(firstCreateRequest.title()));
        var error = spaceHelper.update(context, updateRequest, secondSpaceId).then()
            .statusCode(400)
            .extract().as(ErrorResponseDto.class);

        SpaceJpaEntity space = jpaTemplate.load(secondSpaceId, SpaceJpaEntity.class);

        assertEquals(INVALID_INPUT, error.code());
        assertEquals(secondCreateRequest.title(), space.getTitle());
        assertEquals(space.getCreationTime(), space.getLastModificationTime());
        assertNotNull(error.message());
    }

    @Test
    void updateSpace_withSameTitleAsDeleted() {
        var firstCreateRequest = createSpaceRequestDto();
        // Create first space
        var firstCreateResponse = spaceHelper.create(context, firstCreateRequest);
        firstCreateResponse.then()
            .statusCode(201)
            .body("id", notNullValue());

        Number firstSpaceId = firstCreateResponse.body().path("id");
        // Delete the first space
        spaceHelper.delete(context, firstSpaceId);

        var secondCreateRequest = createSpaceRequestDto();
        // Create second space with different request
        var secondCreateResponse = spaceHelper.create(context, secondCreateRequest);
        secondCreateResponse.then()
            .statusCode(201)
            .body("id", notNullValue());

        Number secondSpaceId = secondCreateResponse.body().path("id");
        // Update the second space's title to match the deleted space's title
        var updateRequest = updateSpaceRequestDto(b -> b.title(firstCreateRequest.title()));
        spaceHelper.update(context, updateRequest, secondSpaceId).then()
            .statusCode(200);

        SpaceJpaEntity space = jpaTemplate.load(secondSpaceId, SpaceJpaEntity.class);

        assertEquals(updateRequest.title(), space.getTitle());
        assertEquals(generateSlugCode(updateRequest.title()), space.getCode());
        assertEquals(context.getCurrentUser().getUserId(), space.getLastModifiedBy());
        assertTrue(space.getLastModificationTime().isAfter(space.getCreationTime()));
    }
}
