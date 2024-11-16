package org.flickit.assessment.scenario.test.users.space;

import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.flickit.assessment.common.exception.api.ErrorCodes.INVALID_INPUT;
import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.scenario.fixture.request.CreateSpaceRequestDtoMother.createSpaceRequestDto;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

class CreateSpaceScenarioTest extends AbstractScenarioTest {

    @Autowired
    SpaceTestHelper spaceHelper;

    @Test
    void createSpace() {
        var request = createSpaceRequestDto();
        var response = spaceHelper.create(context, request);

        response.then()
            .statusCode(201)
            .body("id", notNullValue());

        Number spaceId = response.path("id");

        SpaceJpaEntity loadedSpace = jpaTemplate.load(spaceId, SpaceJpaEntity.class);
        assertEquals(request.title(), loadedSpace.getTitle());
        assertEquals(generateSlugCode(request.title()), loadedSpace.getCode());
        assertEquals(getCurrentUserId(), loadedSpace.getOwnerId());
        assertEquals(getCurrentUserId(), loadedSpace.getCreatedBy());
        assertEquals(getCurrentUserId(), loadedSpace.getLastModifiedBy());
        assertNotNull(loadedSpace.getCreationTime());
        assertNotNull(loadedSpace.getLastModificationTime());
        assertEquals(0, loadedSpace.getDeletionTime());

        boolean userAccessExists = jpaTemplate.existById(
            new SpaceUserAccessJpaEntity.EntityId(spaceId.longValue(), getCurrentUserId()),
            SpaceUserAccessJpaEntity.class);
        assertTrue(userAccessExists);
    }

    @Test
    void createSpace_duplicateTitle() {
        final var request = createSpaceRequestDto();
        // First invoke
        var response = spaceHelper.create(context, request);
        response.then()
            .statusCode(201);

        final int countBefore = jpaTemplate.count(SpaceJpaEntity.class);

        // Second invoke with the same request
        var response2 = spaceHelper.create(context, request);
        var error = response2.then()
            .statusCode(400)
            .extract().as(ErrorResponseDto.class);

        assertEquals(INVALID_INPUT, error.code());
        assertNotNull(error.message());

        int countAfter = jpaTemplate.count(SpaceJpaEntity.class);
        assertEquals(countBefore, countAfter);
    }
}
