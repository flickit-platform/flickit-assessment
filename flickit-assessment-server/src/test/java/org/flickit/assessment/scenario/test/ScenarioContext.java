package org.flickit.assessment.scenario.test;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import okhttp3.mockwebserver.MockWebServer;
import org.flickit.assessment.scenario.test.users.user.UserTestHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.flickit.assessment.scenario.fixture.auth.JwtTokenTestUtils.generateJwtToken;

public class ScenarioContext {

    @Autowired
    private UserTestHelper userTestHelper;

    @Getter
    @Setter
    private MockWebServer mockDslWebServer;

    private CurrentUser currentUser;
    private final Consumer<CurrentUser> changeCurrentUserListener;


    public ScenarioContext(Consumer<CurrentUser> changeCurrentUserListener) {
        this.changeCurrentUserListener = changeCurrentUserListener;
        this.currentUser = getCurrentUser();
    }

    public ScenarioContext() {
        this.changeCurrentUserListener = null;
    }

    public CurrentUser getCurrentUser() {
        checkNotNull(changeCurrentUserListener);
        if (currentUser == null) {
            currentUser = new CurrentUser(UUID.randomUUID());
            changeCurrentUserListener.accept(currentUser);
        }
        return currentUser;
    }

    public CurrentUser getNextCurrentUser() {
        clearUser();
        return getCurrentUser();
    }

    private void clearUser() {
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
