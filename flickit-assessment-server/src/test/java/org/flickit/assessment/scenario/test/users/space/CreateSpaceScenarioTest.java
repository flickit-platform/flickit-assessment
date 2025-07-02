package org.flickit.assessment.scenario.test.users.space;

import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.flickit.assessment.users.application.domain.SpaceStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.flickit.assessment.common.exception.api.ErrorCodes.INVALID_INPUT;
import static org.flickit.assessment.common.exception.api.ErrorCodes.UPGRADE_REQUIRED;
import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.scenario.fixture.request.CreateSpaceRequestDtoMother.createSpaceRequestDto;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

class CreateSpaceScenarioTest extends AbstractScenarioTest {

    @Autowired
    SpaceTestHelper spaceHelper;

    @Autowired
    AppSpecProperties appSpecProperties;

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
        assertEquals(SpaceStatus.ACTIVE.getId(), loadedSpace.getStatus());
        assertEquals(getCurrentUserId(), loadedSpace.getCreatedBy());
        assertEquals(getCurrentUserId(), loadedSpace.getLastModifiedBy());
        assertNotNull(loadedSpace.getCreationTime());
        assertNotNull(loadedSpace.getLastModificationTime());
        assertEquals(0, loadedSpace.getDeletionTime());
        assertFalse(loadedSpace.isDeleted());
        assertFalse(loadedSpace.isDefault());

        boolean userAccessExists = jpaTemplate.existById(
            new SpaceUserAccessJpaEntity.EntityId(spaceId.longValue(), getCurrentUserId()),
            SpaceUserAccessJpaEntity.class);
        assertTrue(userAccessExists);
    }

    @Test
    void createSpace_duplicateTitle() {
        final var request = createSpaceRequestDto(b -> b.type(SpaceType.PREMIUM.getCode()));
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
        // First invoke
        var firstCreateResponse = spaceHelper.create(context, request);
        firstCreateResponse.then()
            .statusCode(201);

        Number createdSpaceId = firstCreateResponse.body().path("id");

        // Delete created space
        spaceHelper.delete(context, createdSpaceId)
            .then()
            .statusCode(204);

        final int countBefore = jpaTemplate.count(SpaceJpaEntity.class);

        // Second invoke with the same request
        spaceHelper.create(context, request).then()
            .statusCode(201);

        final int countAfter = jpaTemplate.count(SpaceJpaEntity.class);

        assertEquals(countBefore + 1, countAfter);
    }

    @Test
    void createSpace_withBasicSpaceLimitReached() {
        var limit = appSpecProperties.getSpace().getMaxBasicSpaces();
        // #basic = limit
        createBasicSpaces(limit);

        final int countBefore = jpaTemplate.count(SpaceJpaEntity.class);

        final var request = createSpaceRequestDto();
        // create new space
        var response = spaceHelper.create(context, request);
        var error = response.then()
            .statusCode(403)
            .extract().as(ErrorResponseDto.class);

        assertEquals(UPGRADE_REQUIRED, error.code());
        assertNotNull(error.message());

        final int countAfter = jpaTemplate.count(SpaceJpaEntity.class);

        assertEquals(countBefore, countAfter);
    }

    private void createBasicSpaces(int limit) {
        for (int i = 0; i < limit; i++) {
            var request = createSpaceRequestDto();
            spaceHelper.create(context, request);
        }
    }
}
