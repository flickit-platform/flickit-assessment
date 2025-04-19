package org.flickit.assessment.scenario.test.users.space;

import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.flickit.assessment.scenario.fixture.request.CreateSpaceRequestDtoMother.createSpaceRequestDto;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GetSpaceScenarioTest extends AbstractScenarioTest {

    @Autowired
    SpaceTestHelper spaceHelper;

    @Test
    void getSpace() {
        var createRequest = createSpaceRequestDto();
        var createResponse = spaceHelper.create(context, createRequest);

        createResponse.then()
            .statusCode(201)
            .body("id", notNullValue());

        Number spaceId = createResponse.body().path("id");
        spaceHelper.get(context, spaceId).then()
            .statusCode(200);

        SpaceJpaEntity space = jpaTemplate.load(spaceId, SpaceJpaEntity.class);
        assertNotNull(space);
    }
}
