package org.flickit.assessment.scenario.test.users.space;

import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        SpaceJpaEntity space = jpaTemplate.load(spaceId, SpaceJpaEntity.class);
        spaceHelper.delete(context, spaceId.toString()).then()
            .statusCode(204);

        SpaceJpaEntity deletedSpace = jpaTemplate.load(spaceId, SpaceJpaEntity.class);
        assertFalse(space.isDeleted());
        assertTrue(deletedSpace.isDeleted());
    }
}
