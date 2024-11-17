package org.flickit.assessment.scenario.test;

import lombok.Value;
import org.flickit.assessment.scenario.test.users.user.UserTestHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.scenario.fixture.auth.JwtTokenTestUtils.generateJwtToken;

public class ScenarioContext {

    @Autowired
    private UserTestHelper userTestHelper;

    private CurrentUser currentUser;
    private final Consumer<CurrentUser> changeCurrentUserListener;

    public ScenarioContext(Consumer<CurrentUser> changeCurrentUserListener) {
        this.changeCurrentUserListener = changeCurrentUserListener;
        this.currentUser = getCurrentUser();
    }

    public CurrentUser getCurrentUser() {
        if (currentUser == null) {
            currentUser = new CurrentUser(UUID.randomUUID());
            changeCurrentUserListener.accept(currentUser);
        }
        return currentUser;
    }

    public CurrentUser getNextCurrentUser() {
        clear();
        return getCurrentUser();
    }

    private void clear() {
        currentUser = null;
    }

    @Value
    public static class CurrentUser {

        UUID userId;
        String jwt;

        public CurrentUser(UUID userId) {
            this.userId = userId;
            this.jwt = generateJwtToken(userId);
        }
    }
}
