package org.flickit.assessment.kit.application.service.user;

import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitByIdPort;
import org.flickit.assessment.kit.application.port.out.kituser.LoadKitUserByKitAndUserPort;
import org.flickit.assessment.kit.application.port.out.user.DeleteUserAccessPort;
import org.flickit.assessment.kit.application.port.out.user.LoadUserByIdPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DeleteUserAccessServiceTest {

    @InjectMocks
    private DeleteUserAccessService service;

    @Mock
    private DeleteUserAccessPort deleteUserAccessPort;

    @Mock
    private LoadKitByIdPort loadKitByIdPort;

    @Mock
    private LoadUserByIdPort loadUserByIdPort;

    @Mock
    private LoadKitUserByKitAndUserPort loadKitUserByKitAndUserPort;

    @Test
    void testDeleteUserAccess_ValidInputs_Delete() {

    }


}
