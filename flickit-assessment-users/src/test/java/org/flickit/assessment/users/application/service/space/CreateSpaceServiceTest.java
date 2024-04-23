package org.flickit.assessment.users.application.service.space;

import org.apache.commons.lang3.RandomStringUtils;
import org.flickit.assessment.users.application.port.in.space.CreateSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.CreateSpacePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class CreateSpaceServiceTest {

    @InjectMocks
    CreateSpaceService service;
    @Mock
    CreateSpacePort createSpacePort;

    @Test
    @DisplayName("inserting new space with valid parameters should not throws any exception")
    void testCreateSpace_validParams_successful() {
        String title = RandomStringUtils.randomAlphabetic(10);
        UUID currentUserId = UUID.randomUUID();
        CreateSpaceUseCase.Param param = new CreateSpaceUseCase.Param(title, currentUserId);

        doNothing().when(createSpacePort).persist(any());
        assertDoesNotThrow(()-> service.createSpace(param));
    }
}
