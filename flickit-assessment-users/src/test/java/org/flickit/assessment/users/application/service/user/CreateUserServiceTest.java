package org.flickit.assessment.users.application.service.user;

import org.flickit.assessment.users.application.port.in.user.CreateUserUseCase;
import org.flickit.assessment.users.application.port.out.user.CreateUserPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateUserServiceTest {

    @InjectMocks
    private CreateUserService service;

    @Mock
    private CreateUserPort createUserPort;

    @Test
    void testCreateUserService_ValidInput_ValidResult() {
        UUID userId = UUID.randomUUID();
        String email = "admin@flickit.org";
        String displayName = "Flickit Admin";
        CreateUserUseCase.Param param = new CreateUserUseCase.Param(userId,
            email,
            displayName);
        when(createUserPort.persist(any(UUID.class), any(), any())).thenReturn(userId);

        CreateUserUseCase.Result result = service.createUser(param);

        assertNotNull(result);
        assertEquals(userId, result.userId());
    }
}
