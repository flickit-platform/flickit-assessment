package org.flickit.assessment.scenario.test.users.space;

import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
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
    void deleteSpaceByNonOwnerUser() {
        var createRequest = createSpaceRequestDto();
        var createResponse = spaceHelper.create(context, createRequest);

        createResponse.then()
            .statusCode(201)
            .body("id", notNullValue());

        Number spaceId = createResponse.body().path("id");
        context.getNextCurrentUser();
        var deleteResponse = spaceHelper.delete(context, spaceId.toString());

        deleteResponse.then()
            .statusCode(403);

        SpaceJpaEntity space = jpaTemplate.load(spaceId, SpaceJpaEntity.class);
        assertFalse(space.isDeleted());
    }
}