package org.flickit.assessment.users.application.service.user;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.in.user.GetUserIdByEmailUseCase;
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
class GetUserIdByEmailServiceTest {

    @InjectMocks
    private GetUserIdByEmailService service;

    @Mock
    private LoadUserPort loadUserPort;

    @Test
    void testGetUserIdByEmail_ValidParams_ReturnUserIdSuccessfully() {
        GetUserIdByEmailUseCase.Param param = new GetUserIdByEmailUseCase.Param("user@email.com");
        UUID expectedUserId = UUID.randomUUID();
        when(loadUserPort.loadUserIdByEmail(param.getEmail())).thenReturn(Optional.of(expectedUserId));

        UUID id = service.getUserIdByEmail(param);

        var emailParam = ArgumentCaptor.forClass(String.class);
        verify(loadUserPort, times(1)).loadUserIdByEmail(emailParam.capture());
        assertEquals(param.getEmail(), emailParam.getValue());

        assertEquals(expectedUserId, id);
    }

    @Test
    void testGetUserIdByEmail_InvalidEmail_ThrowsException() {
        GetUserIdByEmailUseCase.Param param = new GetUserIdByEmailUseCase.Param("user@email.com");
        when(loadUserPort.loadUserIdByEmail(param.getEmail()))
            .thenThrow(new ResourceNotFoundException(USER_BY_EMAIL_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class,
            () -> service.getUserIdByEmail(param));

        assertEquals(USER_BY_EMAIL_NOT_FOUND, throwable.getMessage());
    }
}
