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
        // First invoke
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
        var createRequestFirst = createSpaceRequestDto();
        var createRequestSecond = createSpaceRequestDto();

        var createResponseFirst = spaceHelper.create(context, createRequestFirst);
        createResponseFirst.then()
            .statusCode(201)
            .body("id", notNullValue());

        var createResponseSecond = spaceHelper.create(context, createRequestSecond);
        createResponseSecond.then()
            .statusCode(201)
            .body("id", notNullValue());

        Number spaceIdSecond = createResponseSecond.body().path("id");

        var updateRequest = createSpaceRequestDto(b -> b.title(createRequestFirst.title()));
        var error = spaceHelper.update(context, updateRequest, spaceIdSecond).then()
            .statusCode(400)
            .extract().as(ErrorResponseDto.class);

        SpaceJpaEntity space = jpaTemplate.load(spaceIdSecond, SpaceJpaEntity.class);

        assertEquals(INVALID_INPUT, error.code());
        assertEquals(createRequestSecond.title(), space.getTitle());
        assertEquals(space.getCreationTime(), space.getLastModificationTime());
        assertNotNull(error.message());
    }
}
