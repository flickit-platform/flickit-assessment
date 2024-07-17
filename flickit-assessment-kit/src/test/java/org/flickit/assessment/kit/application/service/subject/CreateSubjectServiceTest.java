package org.flickit.assessment.kit.application.service.subject;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.subject.CreateSubjectUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.subject.CreateSubjectPort;
import org.junit.jupiter.api.BeforeEach;
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
class CreateSubjectServiceTest {

    @InjectMocks
    private CreateSubjectService createSubjectService;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private CreateSubjectPort createSubjectPort;

    private long kitVersionId;
    private int index;
    private String title;
    private String description;
    private int weight;
    private long expertGroupId;
    private UUID currentUserId;
    private UUID ownerId;

    @BeforeEach
    void setUp() {
        kitVersionId = 1;
        index = 2;
        title = "team";
        description = "about team";
        weight = 3;
        expertGroupId = 4;
        currentUserId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
    }

    @Test
    void testCreateSubject_WhenCurrentUserIsNotOwner_ShouldThrowAccessDeniedException() {
        CreateSubjectUseCase.Param param = new CreateSubjectUseCase.Param(kitVersionId,
            index,
            title,
            description,
            weight,
            expertGroupId,
            currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(ownerId);

        assertThrows(AccessDeniedException.class, () -> createSubjectService.createSubject(param));
    }

    @Test
    void testCreateSubject_WhenCurrentUserIsOwner_ShouldCreateSubject() {
        long subscriberId = 1;
        currentUserId = ownerId;
        CreateSubjectUseCase.Param param = new CreateSubjectUseCase.Param(kitVersionId,
            index,
            title,
            description,
            weight,
            expertGroupId,
            currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(ownerId);
        when(createSubjectPort.persist(any(CreateSubjectPort.Param.class))).thenReturn(subscriberId);

        long actualSubject = createSubjectService.createSubject(param);
        assertEquals(subscriberId, actualSubject);
    }
}
