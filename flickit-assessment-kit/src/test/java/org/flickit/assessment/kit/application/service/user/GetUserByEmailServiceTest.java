package org.flickit.assessment.kit.application.service.user;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.User;
import org.flickit.assessment.kit.application.port.in.user.GetUserByEmailUseCase;
import org.flickit.assessment.kit.application.port.out.user.LoadUserByEmailPort;
import org.flickit.assessment.kit.test.fixture.application.UserMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_USER_BY_EMAIL_EMAIL_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserByEmailServiceTest {

    @InjectMocks
    private GetUserByEmailService service;

    @Mock
    private LoadUserByEmailPort loadUserByEmailPort;

    @Test
    void testGetUserByEmail_ValidParams_ReturnUserIdSuccessfully() {
        GetUserByEmailUseCase.Param param = new GetUserByEmailUseCase.Param(
            "user@email.com"
        );
        UUID expectedUserId = UUID.randomUUID();
        Optional<User> user = Optional.of(UserMother.userWithId(expectedUserId));
        when(loadUserByEmailPort.loadByEmail(param.getEmail())).thenReturn(user);

        UUID id = service.getUserByEmail(param);

        var emailParam = ArgumentCaptor.forClass(String.class);
        verify(loadUserByEmailPort, times(1)).loadByEmail(emailParam.capture());
        assertEquals(param.getEmail(), emailParam.getValue());

        assertEquals(expectedUserId, id);
    }

    @Test
    void testGetUserByEmail_InvalidEmail_ThrowsException() {
        GetUserByEmailUseCase.Param param = new GetUserByEmailUseCase.Param(
            "user@email.com"
        );
        when(loadUserByEmailPort.loadByEmail(param.getEmail())).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class,
            () -> service.getUserByEmail(param));

        assertEquals(GET_USER_BY_EMAIL_EMAIL_NOT_FOUND, exception.getMessage());
    }
}
