package org.flickit.assessment.scenario.test.users.space;

import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.exception.api.ErrorCodes.ACCESS_DENIED;
import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.scenario.fixture.request.CreateSpaceRequestDtoMother.createSpaceRequestDto;
import static org.flickit.assessment.scenario.fixture.request.UpdateSpaceRequestDtoMother.updateSpaceRequestDto;
import static org.junit.jupiter.api.Assertions.*;

class UpdateSpaceScenarioTest extends AbstractScenarioTest {

    @Autowired
    SpaceTestHelper spaceHelper;

    @Test
    void updateSpace() {
        var spaceId = createSpace();
        SpaceJpaEntity createdSpace = jpaTemplate.load(spaceId, SpaceJpaEntity.class);

        var updateRequest = updateSpaceRequestDto();
        spaceHelper.update(context, updateRequest, spaceId).then()
                .statusCode(200);

        SpaceJpaEntity updatedSpace = jpaTemplate.load(spaceId, SpaceJpaEntity.class);

        assertEquals(updateRequest.title(), updatedSpace.getTitle());
        assertEquals(generateSlugCode(updateRequest.title()), updatedSpace.getCode());
        assertEquals(getCurrentUserId(), updatedSpace.getLastModifiedBy());
        assertThat(updatedSpace.getLastModificationTime()).isAfter(createdSpace.getLastModificationTime());
    }


    @Test
    void updateSpace_userIsNotOwner() {
        // Create space
        Number spaceId = createSpace();

        // Change currentUser which is not owner (creator) of the space
        context.getNextCurrentUser();

        var updateRequest = updateSpaceRequestDto();
        var error = spaceHelper.update(context, updateRequest, spaceId).then()
                .statusCode(403)
                .extract().as(ErrorResponseDto.class);

        SpaceJpaEntity space = jpaTemplate.load(spaceId, SpaceJpaEntity.class);

        assertEquals(space.getCreationTime(), space.getLastModificationTime());
        assertEquals(ACCESS_DENIED, error.code());
        assertNotNull(error.message());
    }

    @Test
    void updateSpace_withSameTitle() {
        // Create first space
        Number firstSpaceId = createSpace();
        SpaceJpaEntity firstSpace = jpaTemplate.load(firstSpaceId, SpaceJpaEntity.class);

        // Create second space with different title
        Number secondSpaceId = createSpace();

        // Update the second space's title to match the first space's title
        var updateRequest = updateSpaceRequestDto(b -> b.title(firstSpace.getTitle()));
        spaceHelper.update(context, updateRequest, secondSpaceId).then()
                .statusCode(200);

        SpaceJpaEntity updatedSpace = jpaTemplate.load(secondSpaceId, SpaceJpaEntity.class);

        assertEquals(firstSpace.getTitle(), updatedSpace.getTitle());
        assertTrue(updatedSpace.getLastModificationTime().isAfter(updatedSpace.getCreationTime()));
    }

    private long createSpace() {
        var request = createSpaceRequestDto();
        var response = spaceHelper.create(context, request);
        Number id = response.path("id");
        return id.longValue();
    }
}
