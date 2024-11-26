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

    @Test
    void createSpace_withSameTitleAsDeleted() {
        final var request = createSpaceRequestDto();
        var firstCreateResponse = spaceHelper.create(context, request);
        firstCreateResponse.then()
            .statusCode(201);

        final int countBeforeDelete = jpaTemplate.count(SpaceJpaEntity.class);
        var createdSpaceId = firstCreateResponse.body().path("id");
        SpaceJpaEntity space = jpaTemplate.load(createdSpaceId, SpaceJpaEntity.class);


        var deleteResponse = spaceHelper.delete(context, createdSpaceId.toString());
        deleteResponse.then()
            .statusCode(204);

        final int countAfterDelete = jpaTemplate.count(SpaceJpaEntity.class);
        SpaceJpaEntity deletedSpace = jpaTemplate.load(createdSpaceId, SpaceJpaEntity.class);

        var secondCreateResponseWithSameTitle = spaceHelper.create(context, request);
        secondCreateResponseWithSameTitle.then()
            .statusCode(201);

        final int countAfterSecondCreation = jpaTemplate.count(SpaceJpaEntity.class);
        var newSpaceId = secondCreateResponseWithSameTitle.body().path("id");
        SpaceJpaEntity secondSpace = jpaTemplate.load(newSpaceId, SpaceJpaEntity.class);

        assertEquals(1, countBeforeDelete);
        assertFalse(space.isDeleted());
        assertEquals(1, countAfterDelete);
        assertTrue(deletedSpace.isDeleted());
        assertEquals(2, countAfterSecondCreation);
        assertFalse(secondSpace.isDeleted());
    }
}
