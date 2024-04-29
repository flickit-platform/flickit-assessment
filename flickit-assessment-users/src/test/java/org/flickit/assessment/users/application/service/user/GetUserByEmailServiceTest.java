package org.flickit.assessment.users.application.service.user;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.in.user.GetUserByEmailUseCase;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.USER_BY_EMAIL_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserByEmailServiceTest {

    @InjectMocks
    private GetUserByEmailService service;

    @Mock
    private LoadUserPort loadUserPort;

    @Test
    void testGetUserByEmail_ValidParams_ReturnUserIdSuccessfully() {
        GetUserByEmailUseCase.Param param = new GetUserByEmailUseCase.Param("user@email.com");
        UUID expectedUserId = UUID.randomUUID();
        when(loadUserPort.loadUserIdByEmail(param.getEmail())).thenReturn(Optional.of(expectedUserId));

        UUID id = service.getUserByEmail(param);

        var emailParam = ArgumentCaptor.forClass(String.class);
        verify(loadUserPort, times(1)).loadUserIdByEmail(emailParam.capture());
        assertEquals(param.getEmail(), emailParam.getValue());

        assertEquals(expectedUserId, id);
    }

    @Test
    void testGetUserByEmail_InvalidEmail_ThrowsException() {
        GetUserByEmailUseCase.Param param = new GetUserByEmailUseCase.Param("user@email.com");
        when(loadUserPort.loadUserIdByEmail(param.getEmail()))
            .thenThrow(new ResourceNotFoundException(USER_BY_EMAIL_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class,
            () -> service.getUserByEmail(param));

        assertEquals(USER_BY_EMAIL_NOT_FOUND, throwable.getMessage());
    }
}
