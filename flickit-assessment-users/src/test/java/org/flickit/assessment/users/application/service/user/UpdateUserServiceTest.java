package org.flickit.assessment.users.application.service.user;

import org.flickit.assessment.users.application.domain.User;
import org.flickit.assessment.users.application.port.in.user.UpdateUserUseCase;
import org.flickit.assessment.users.application.port.out.user.UpdateUserPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateUserServiceTest {

    @InjectMocks
    private UpdateUserService service;

    @Mock
    private UpdateUserPort port;

    @Test
    void testUpdateUser_ValidInput_ValidResult() {
        UUID userId = UUID.randomUUID();
        String displayName = "Flickit Admin";
        String bio = "Admin bio";
        String linkedin = "linkedin.com/in/flickit-admin";
        UpdateUserUseCase.Param param = new UpdateUserUseCase.Param(userId,
            displayName,
            bio,
            linkedin);

        User expectedUser = new User(userId,
            "admin@flickit.org",
            displayName,
            bio,
            linkedin,
            null);

        when(port.updateUser(any(UpdateUserPort.Param.class))).thenReturn(expectedUser);

        User actualUser = service.updateUser(param);

        assertEquals(expectedUser, actualUser);
    }

}
