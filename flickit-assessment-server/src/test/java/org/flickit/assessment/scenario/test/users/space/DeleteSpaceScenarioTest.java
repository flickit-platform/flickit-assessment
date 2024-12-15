package org.flickit.assessment.scenario.test.users.space;

import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.exception.api.ErrorCodes.ACCESS_DENIED;
import static org.flickit.assessment.scenario.fixture.request.CreateSpaceRequestDtoMother.createSpaceRequestDto;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

class DeleteSpaceScenarioTest extends AbstractScenarioTest {

    @Autowired
    SpaceTestHelper spaceHelper;

    @Test
    void deleteSpace() {
        var createRequest = createSpaceRequestDto();
        var createResponse = spaceHelper.create(context, createRequest);

        createResponse.then()
            .statusCode(201)
            .body("id", notNullValue());

        Number spaceId = createResponse.body().path("id");
        spaceHelper.delete(context, spaceId.toString()).then()
            .statusCode(204);

        SpaceJpaEntity deletedSpace = jpaTemplate.load(spaceId, SpaceJpaEntity.class);
        assertTrue(deletedSpace.isDeleted());
        assertThat(deletedSpace.getDeletionTime()).isPositive();
    }

    @Test
    void deleteSpace_notAllowed() {
        // Create a space
        var createRequest = createSpaceRequestDto();
        var createResponse = spaceHelper.create(context, createRequest);

        createResponse.then()
            .statusCode(201);

        Number spaceId = createResponse.body().path("id");
        // Change currentUser which is not owner (creator) of the space
        context.getNextCurrentUser();

        // Delete space by non owner user
        var response = spaceHelper.delete(context, spaceId.toString());

        var error = response.then()
            .statusCode(403)
            .extract().as(ErrorResponseDto.class);

        assertEquals(ACCESS_DENIED, error.code());
        assertNotNull(error.message());

        SpaceJpaEntity space = jpaTemplate.load(spaceId, SpaceJpaEntity.class);
        assertFalse(space.isDeleted());
        assertEquals(0, space.getDeletionTime());
    }
}
