package org.flickit.assessment.kit.application.service.maturitylevel;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.port.in.maturitylevel.CreateMaturityLevelUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.CreateMaturityLevelPort;
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
class CreateMaturityLevelServiceTest {

    @InjectMocks
    private CreateMaturityLevelService createMaturityLevelService;

    @Mock
    private CreateMaturityLevelPort createMaturityLevelPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    private int index;
    private String title;
    private String description;
    private Integer value;
    private UUID currentUserId;
    private UUID ownerId;
    private AssessmentKit kit;

    @BeforeEach
    void setUp() {
        index = 1;
        title = "basic";
        description = "basic level indicating fundamental and essential functionalities of the system.";
        value = 1;
        currentUserId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        kit = AssessmentKitMother.simpleKit();
    }

    @Test
    void testCreateMaturityLevel_WhenCurrentUserIsNotOwner_ShouldThrowAccessDeniedException() {
        CreateMaturityLevelUseCase.Param param = new CreateMaturityLevelUseCase.Param(kit.getId(),
            index,
            title,
            description,
            value,
            currentUserId);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);

        assertThrows(AccessDeniedException.class, () -> createMaturityLevelService.createMaturityLevel(param));
    }

    @Test
    void testCreateMaturityLevel_WhenCurrentUserIsOwner_ThenCreateMaturityLevel() {
        ownerId = currentUserId;
        long levelId = 1;
        CreateMaturityLevelUseCase.Param param = new CreateMaturityLevelUseCase.Param(kit.getId(),
            index,
            title,
            description,
            value,
            currentUserId);
        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);
        when(createMaturityLevelPort.persist(any(MaturityLevel.class), anyLong(), any(UUID.class))).thenReturn(levelId);

        long actualLevelId = createMaturityLevelService.createMaturityLevel(param);
        assertEquals(levelId, actualLevelId);
    }
}
