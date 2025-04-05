package org.flickit.assessment.scenario.test.users.space;

import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.flickit.assessment.common.exception.api.ErrorCodes.ACCESS_DENIED;
import static org.flickit.assessment.common.exception.api.ErrorCodes.INVALID_INPUT;
import static org.flickit.assessment.scenario.fixture.request.CreateSpaceRequestDtoMother.createSpaceRequestDto;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

public class UpdateSpaceScenarioTest extends AbstractScenarioTest {

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

        var updateRequest = createSpaceRequestDto(b -> b.title("newTitle"));
        spaceHelper.update(context, updateRequest, spaceId).then()
            .statusCode(200);

        SpaceJpaEntity updatedSpace = jpaTemplate.load(spaceId, SpaceJpaEntity.class);

        assertEquals("newTitle", updatedSpace.getTitle());
        assertTrue(updatedSpace.getLastModificationTime().isAfter(createdSpace.getLastModificationTime()));
    }

    @Test
    void updateSpace_userIsNotOwner() {
        var createRequest = createSpaceRequestDto();
        var createResponse = spaceHelper.create(context, createRequest);

        createResponse.then()
            .statusCode(201)
            .body("id", notNullValue());

        Number spaceId = createResponse.body().path("id");

        var updateRequest = createSpaceRequestDto(b -> b.title("newTitle"));
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
    void updateSpace_titleIsDuplicated() {
        var firstCreateRequest = createSpaceRequestDto();
        var secondRequestSecond = createSpaceRequestDto();
        // First invoke
        var firstCreateResponse = spaceHelper.create(context, firstCreateRequest);
        firstCreateResponse.then()
            .statusCode(201)
            .body("id", notNullValue());
        // Second invoke with different request
        var secondCreateResponse = spaceHelper.create(context, secondRequestSecond);
        secondCreateResponse.then()
            .statusCode(201)
            .body("id", notNullValue());

        Number secondSpaceId = secondCreateResponse.body().path("id");
        // Update the second space's title to match the first space's title
        var updateRequest = createSpaceRequestDto(b -> b.title(firstCreateRequest.title()));
        var error = spaceHelper.update(context, updateRequest, secondSpaceId).then()
            .statusCode(400)
            .extract().as(ErrorResponseDto.class);

        SpaceJpaEntity space = jpaTemplate.load(secondSpaceId, SpaceJpaEntity.class);

        assertEquals(INVALID_INPUT, error.code());
        assertEquals(secondRequestSecond.title(), space.getTitle());
        assertEquals(space.getCreationTime(), space.getLastModificationTime());
        assertNotNull(error.message());
    }

    @Test
    void updateSpace_titleIsTheSameAsDeletedSpace() {
        var firstCreateRequest = createSpaceRequestDto();
        var secondCreateRequest = createSpaceRequestDto();
        // First invoke
        var firstCreateResponse = spaceHelper.create(context, firstCreateRequest);
        firstCreateResponse.then()
            .statusCode(201)
            .body("id", notNullValue());

        Number firstSpaceId = firstCreateResponse.body().path("id");
        spaceHelper.delete(context, firstSpaceId);
        // Second invoke with different request
        var secondCreateResponse = spaceHelper.create(context, secondCreateRequest);
        secondCreateResponse.then()
            .statusCode(201)
            .body("id", notNullValue());

        Number secondSpaceId = secondCreateResponse.body().path("id");
        // Update the second space's title to match the deleted space's title
        var updateRequest = createSpaceRequestDto(b -> b.title(firstCreateRequest.title()));
        spaceHelper.update(context, updateRequest, secondSpaceId).then()
            .statusCode(200);

        SpaceJpaEntity space = jpaTemplate.load(secondSpaceId, SpaceJpaEntity.class);

        assertEquals(updateRequest.title(), space.getTitle());
        assertTrue(space.getLastModificationTime().isAfter(space.getCreationTime()));
    }
}
