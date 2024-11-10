package org.flickit.assessment.scenario.test.users.user;

import org.flickit.assessment.common.exception.api.ErrorResponseDto;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.junit.jupiter.api.Test;

import static org.flickit.assessment.common.exception.api.ErrorCodes.INVALID_INPUT;
import static org.flickit.assessment.scenario.fixture.request.CreateUserRequestDtoMother.createUserRequestDto;
import static org.junit.jupiter.api.Assertions.*;

class CreateUserScenarioTest extends AbstractScenarioTest {

    @Override
    protected boolean enableCreateCurrentUser() {
        return false;
    }

    @Test
    void createUser() {
        var request = createUserRequestDto();

        var response = userHelper.create(request);

        response.then()
            .statusCode(201);

        UserJpaEntity loadedUser = jpaTemplate.load(request.id(), UserJpaEntity.class);

        assertEquals(request.email(), loadedUser.getEmail());
        assertEquals(request.displayName(), loadedUser.getDisplayName());
        assertTrue(loadedUser.getIsActive());
        assertFalse(loadedUser.getIsSuperUser());
        assertFalse(loadedUser.getIsStaff());
        assertNull(loadedUser.getBio());
        assertNull(loadedUser.getLinkedin());
        assertNull(loadedUser.getPicture());
        assertNull(loadedUser.getLastLogin());
        assertNotNull(loadedUser.getPassword());
    }

    @Test
    void createUser_duplicateEmail() {
        var request = createUserRequestDto();
        // First invoke
        var response = userHelper.create(request);
        response.then()
            .statusCode(201);

        final int countBefore = jpaTemplate.count(UserJpaEntity.class);

        // Second invoke with the same email
        var duplicateRequest = createUserRequestDto(b -> b.email(request.email()));
        var response2 = userHelper.create(duplicateRequest);

        var error = response2.then()
            .statusCode(400)
            .extract().as(ErrorResponseDto.class);

        assertEquals(INVALID_INPUT, error.code());
        assertNotNull(error.message());

        int countAfter = jpaTemplate.count(UserJpaEntity.class);
        assertEquals(countBefore, countAfter);
    }
}
