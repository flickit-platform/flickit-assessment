package org.flickit.assessment.scenario.test.users.spaceuseraccess;

import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.flickit.assessment.scenario.test.users.space.SpaceTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.flickit.assessment.common.exception.api.ErrorCodes.*;
import static org.flickit.assessment.scenario.fixture.request.AddSpaceMemberRequestDtoMother.addSpaceMemberRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateSpaceRequestDtoMother.createSpaceRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateUserRequestDtoMother.createUserRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AddSpaceMemberScenarioTest extends AbstractScenarioTest {

    @Autowired
    private SpaceUserAccessTestHelper spaceUserAccessHelper;

    @Autowired
    private SpaceTestHelper spaceHelper;

    @Test
    void addSpaceMember() {
        // Create a space
        var createSpaceResponse = spaceHelper.create(context, createSpaceRequestDto());
        createSpaceResponse.then()
            .statusCode(201);

        final Number spaceId = createSpaceResponse.path("id");

        // Create a user intended for addition to the space
        var createInviteeUserRequest = createUserRequestDto();
        var createInviteeUserResponse = userHelper.create(createInviteeUserRequest);
        createInviteeUserResponse.then()
            .statusCode(201);

        // Add the created user to the space
        var request = addSpaceMemberRequestDto(b -> b.email(createInviteeUserRequest.email()));
        var response = spaceUserAccessHelper.create(context, spaceId.longValue(), request);
        response.then()
            .statusCode(200);

        // Check access of the added user
        var loadedSpaceUserAccess = jpaTemplate.load(
            new SpaceUserAccessJpaEntity.EntityId(spaceId.longValue(), createInviteeUserRequest.id()),
            SpaceUserAccessJpaEntity.class);
        assertEquals(getCurrentUserId(), loadedSpaceUserAccess.getCreatedBy());
        assertNotNull(loadedSpaceUserAccess.getCreationTime());
        assertNotNull(loadedSpaceUserAccess.getLastSeen());
    }

    @Test
    void addSpaceMember_notAllowed() {
        // Create a space
        var createSpaceResponse = spaceHelper.create(context, createSpaceRequestDto());
        createSpaceResponse.then()
            .statusCode(201);

        final Number spaceId = createSpaceResponse.path("id");

        // Create a user intended for addition to the space
        var createInviteeUserRequest = createUserRequestDto();
        var createInviteeUserResponse = userHelper.create(createInviteeUserRequest);
        createInviteeUserResponse.then()
            .statusCode(201);

        final int countBefore = jpaTemplate.count(SpaceUserAccessJpaEntity.class);
        // Change currentUser which is not a member of the space
        context.getNextCurrentUser();

        // Add the created user to the space (by the new currentUser)
        var request = addSpaceMemberRequestDto(b -> b.email(createInviteeUserRequest.email()));
        var response = spaceUserAccessHelper.create(context, spaceId.longValue(), request);

        var error = response.then()
            .statusCode(403)
            .extract().as(ErrorResponseDto.class);

        assertEquals(ACCESS_DENIED, error.code());
        assertNotNull(error.message());

        int countAfter = jpaTemplate.count(SpaceUserAccessJpaEntity.class);
        assertEquals(countBefore, countAfter);
    }

    @Test
    void addSpaceMember_duplicateMember() {
        // Create a space
        var createSpaceResponse = spaceHelper.create(context, createSpaceRequestDto());
        createSpaceResponse.then()
            .statusCode(201);

        final Number spaceId = createSpaceResponse.path("id");

        // Create a user intended for addition to the space
        var createInviteeUserRequest = createUserRequestDto();
        var createInviteeUserResponse = userHelper.create(createInviteeUserRequest);
        createInviteeUserResponse.then()
            .statusCode(201);

        // Add the created user to the space (first time)
        var request = addSpaceMemberRequestDto(b -> b.email(createInviteeUserRequest.email()));
        var response = spaceUserAccessHelper.create(context, spaceId.longValue(), request);
        response.then()
            .statusCode(200);

        final int countBefore = jpaTemplate.count(SpaceUserAccessJpaEntity.class);

        // Add the created user to the space (second time)
        var response2 = spaceUserAccessHelper.create(context, spaceId.longValue(), request);
        var error = response2.then()
            .statusCode(409)
            .extract().as(ErrorResponseDto.class);

        assertEquals(ALREADY_EXISTS, error.code());
        assertNotNull(error.message());

        int countAfter = jpaTemplate.count(SpaceUserAccessJpaEntity.class);
        assertEquals(countBefore, countAfter);
    }

    @Test
    void addSpaceMember_userNotExist() {
        // Create a space
        var createSpaceResponse = spaceHelper.create(context, createSpaceRequestDto());
        createSpaceResponse.then()
            .statusCode(201);

        final Number spaceId = createSpaceResponse.path("id");

        final int countBefore = jpaTemplate.count(SpaceUserAccessJpaEntity.class);

        var request = addSpaceMemberRequestDto(); // The request contains email which does not belong to any users
        var response = spaceUserAccessHelper.create(context, spaceId.longValue(), request);
        var error = response.then()
            .statusCode(400)
            .extract().as(ErrorResponseDto.class);

        assertEquals(NOT_FOUND, error.code());
        assertNotNull(error.message());

        int countAfter = jpaTemplate.count(SpaceUserAccessJpaEntity.class);
        assertEquals(countBefore, countAfter);
    }
}
