package org.flickit.assessment.kit.application.service.attribute;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.port.in.attribute.CreateAttributeUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.attribute.CreateAttributePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateAttributeServiceTest {

    @InjectMocks
    private CreateAttributeService createAttributeService;

    @Mock
    private CreateAttributePort createAttributePort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    private int index;
    private String title;
    private String description;
    private int weight;
    private long subjectId;
    private UUID currentUserId;
    private UUID ownerId;
    private AssessmentKit kit;

    @BeforeEach
    void setUp() {
        index = 2;
        title = "team";
        description = "about team";
        weight = 3;
        subjectId = 4;
        currentUserId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        kit = AssessmentKitMother.simpleKit();
    }

    @Test
    void testCreateAttribute_WhenCurrentUserIsNotOwner_ShouldThrowAccessDeniedException() {
        CreateAttributeUseCase.Param param = new CreateAttributeUseCase.Param(kit.getId(),
            index,
            title,
            description,
            weight,
            subjectId,
            currentUserId);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);

        assertThrows(AccessDeniedException.class, () -> createAttributeService.createAttribute(param));
    }

    @Test
    void testCreateAttribute_WhenCurrentUserIsOwner_ThenCreateAttribute() {
        long attributeId = 1;
        currentUserId = ownerId;
        CreateAttributeUseCase.Param param = new CreateAttributeUseCase.Param(kit.getId(),
            index,
            title,
            description,
            weight,
            subjectId,
            currentUserId);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);
        when(createAttributePort.persist(any(Attribute.class), anyLong(), anyLong())).thenReturn(attributeId);

        long actualAttributeId = createAttributeService.createAttribute(param);

        assertEquals(attributeId, actualAttributeId);
    }
}
