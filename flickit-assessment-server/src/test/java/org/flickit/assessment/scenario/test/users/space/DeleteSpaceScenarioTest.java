package org.flickit.assessment.scenario.test.users.space;

import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.flickit.assessment.scenario.test.users.spaceuseraccess.SpaceUserAccessTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.exception.api.ErrorCodes.ACCESS_DENIED;
import static org.flickit.assessment.scenario.fixture.request.AddSpaceMemberRequestDtoMother.addSpaceMemberRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateSpaceRequestDtoMother.createSpaceRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateUserRequestDtoMother.createUserRequestDto;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

class DeleteSpaceScenarioTest extends AbstractScenarioTest {

    @Autowired
    SpaceTestHelper spaceHelper;

    @Autowired
    SpaceUserAccessTestHelper spaceUserAccessHelper;

    @Test
    void deleteSpace() {
        var createRequest = createSpaceRequestDto();
        var createResponse = spaceHelper.create(context, createRequest);

        createResponse.then()
            .statusCode(201)
            .body("id", notNullValue());

        Number spaceId = createResponse.body().path("id");
        spaceHelper.delete(context, spaceId).then()
            .statusCode(204);

        SpaceJpaEntity deletedSpace = jpaTemplate.load(spaceId, SpaceJpaEntity.class);
        assertTrue(deletedSpace.isDeleted());
        assertThat(deletedSpace.getDeletionTime()).isPositive();
    }

    @Test
    void deleteSpace_notAllowed() {
        Number spaceId = createBasicSpace();

        var newMemberId = addMember(spaceId.longValue());

        // Change currentUser which is not a member of the space
        context.getNextCurrentUser();

        // Delete space by non owner user
        var response = spaceHelper.delete(context, spaceId);

        var error = response.then()
            .statusCode(403)
            .extract().as(ErrorResponseDto.class);

        assertEquals(ACCESS_DENIED, error.code());
        assertNotNull(error.message());

        // Change currentUser which is a member (not owner) of the space
        context.setCurrentUser(newMemberId);

        // Delete space by non owner user
        response = spaceHelper.delete(context, spaceId);

        error = response.then()
            .statusCode(403)
            .extract().as(ErrorResponseDto.class);

        assertEquals(ACCESS_DENIED, error.code());
        assertNotNull(error.message());

        SpaceJpaEntity space = jpaTemplate.load(spaceId, SpaceJpaEntity.class);
        assertFalse(space.isDeleted());
        assertEquals(0, space.getDeletionTime());
    }

    private Long createBasicSpace() {
        var response = spaceHelper.create(context, createSpaceRequestDto());
        Number id = response.path("id");
        return id.longValue();
    }

    private UUID addMember(long spaceId) {
        var createUserRequest = createUserRequestDto();
        var createUserResponse = userHelper.create(createUserRequest);
        createUserResponse.then()
            .statusCode(201);

        var request = addSpaceMemberRequestDto(b -> b.email(createUserRequest.email()));
        spaceUserAccessHelper.create(context, spaceId, request);

        return UUID.fromString(createUserResponse.path("userId"));
    }
}
