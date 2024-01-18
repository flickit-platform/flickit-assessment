package org.flickit.assessment.kit.application.service.expertgroup;

import org.flickit.assessment.kit.application.port.in.expertgroup.CreateExpertGroupUseCase.Param;
import org.flickit.assessment.kit.application.port.out.expertgroup.CreateExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CreateExpertGroupAccessPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class CreateExpertGroupServiceTest {

    @InjectMocks
    private CreateExpertGroupService service;
    @Mock
    private CreateExpertGroupPort createExpertGroupPort;
    @Mock
    private CreateExpertGroupAccessPort createExpertGroupAccessPort;

    @Test
    void testCreateExpertGroup_validParams_persistResult() {
        long expectedId = new  Random().nextLong();
        when(createExpertGroupPort.persist(any(CreateExpertGroupPort.Param.class))).thenReturn(expectedId);
        when(createExpertGroupAccessPort.persist(any(CreateExpertGroupAccessPort.Param.class))).thenReturn(new Random().nextLong());

        var result = service.createExpertGroup(param);
        assertNotNull(result, "The result of createExpertGroup service" +
            "should be CreateExpertGroupUseCase.Result");
        assertEquals(expectedId, result.id(), "The result should be long ID");
    }

    @Test
    void testCreateExpertGroup_expertGroupPersistProblem_transactionRollback() {
        when(createExpertGroupPort.persist(any(CreateExpertGroupPort.Param.class))).thenReturn(new Random().nextLong());
        when(createExpertGroupAccessPort.persist(any(CreateExpertGroupAccessPort.Param.class)))
            .thenThrow(new RuntimeException("Simulated exception"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.createExpertGroup(param));
        assertNotNull(exception);

        verify(createExpertGroupPort, times(1)).persist(any(CreateExpertGroupPort.Param.class));
    }

    private final UUID currentUserId = UUID.randomUUID();
    private final Param param = new Param("Expert Group Name",
        "Expert Group Bio",
        "Expert Group About",
        "http://www.example.com",
        null,
        currentUserId);
}
