package org.flickit.assessment.users.application.service.user;

import org.flickit.assessment.users.application.port.in.user.UpdateUserProfileUseCase;
import org.flickit.assessment.users.application.port.out.user.UpdateUserPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class UpdateUserProfileServiceTest {

    @InjectMocks
    private UpdateUserProfileService service;

    @Mock
    private UpdateUserPort updateUserPort;

    @Test
    void testUpdateUserProfile_ValidInput_ValidResult() {
        UUID userId = UUID.randomUUID();
        String displayName = "Flickit Admin";
        String bio = "Admin bio";
        String linkedin = "linkedin.com/in/flickit-admin";
        UpdateUserProfileUseCase.Param param = new UpdateUserProfileUseCase.Param(userId,
            displayName,
            bio,
            linkedin);

        doNothing().when(updateUserPort).updateUser(any());

        assertDoesNotThrow(() -> service.updateUserProfile(param));
    }
}
