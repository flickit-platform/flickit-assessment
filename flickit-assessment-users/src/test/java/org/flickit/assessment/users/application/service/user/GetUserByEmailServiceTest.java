package org.flickit.assessment.users.application.service.user;

import org.apache.commons.lang3.RandomStringUtils;
import org.flickit.assessment.users.application.domain.User;
import org.flickit.assessment.users.application.port.in.user.GetUserByEmailUseCase;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserByEmailServiceTest {

    @InjectMocks
    private GetUserByEmailService service;

    @Mock
    private LoadUserPort loadUserPort;

    @Test
    void testGetUserByEmail_WhenEmailIsValid_ReturnsUser() {
        String mail = "admin@flickit.org";
        User user = new User(UUID.randomUUID(),
            mail,
            "Flickit Admin",
            "admin",
            "linkedin.com/in/flickit",
            "media/flickit.svg");
        LocalDateTime lastLogin = LocalDateTime.now();
        LoadUserPort.Result result = new LoadUserPort.Result(user,
            lastLogin,
            true,
            true,
            true,
            RandomStringUtils.randomAlphanumeric(50));

        when(loadUserPort.loadFullUserByEmail(mail)).thenReturn(result);
        GetUserByEmailUseCase.Param param = new GetUserByEmailUseCase.Param(mail);
        GetUserByEmailUseCase.Result actual = service.getUserByEmail(param);

        assertNotNull(actual);
        assertEquals(result.user(), actual.user());
        assertEquals(result.lastLogin(), actual.lastLogin());
        assertTrue(actual.isSuperUser());
        assertTrue(actual.isStaff());
        assertTrue(actual.isActive());
        assertEquals(result.password(), actual.password());
    }

}
