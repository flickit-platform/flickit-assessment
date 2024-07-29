package org.flickit.assessment.kit.application.service.subject;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.port.in.subject.CreateSubjectUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.subject.CreateSubjectPort;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateSubjectServiceTest {

    @InjectMocks
    private CreateSubjectService createSubjectService;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private CreateSubjectPort createSubjectPort;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    private int index;
    private String title;
    private String description;
    private int weight;
    private UUID currentUserId;
    private UUID ownerId;
    private AssessmentKit kit;

    @BeforeEach
    void setUp() {
        index = 2;
        title = "team";
        description = "about team";
        weight = 3;
        currentUserId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        kit = AssessmentKitMother.simpleKit();
    }

    @Test
    void testCreateSubject_WhenCurrentUserIsNotOwner_ShouldThrowAccessDeniedException() {
        CreateSubjectUseCase.Param param = new CreateSubjectUseCase.Param(kit.getId(),
            index,
            title,
            description,
            weight,
            currentUserId);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);

        assertThrows(AccessDeniedException.class, () -> createSubjectService.createSubject(param));
    }

    @Test
    void testCreateSubject_WhenCurrentUserIsOwner_ShouldCreateSubject() {
        long subscriberId = 1;
        currentUserId = ownerId;
        CreateSubjectUseCase.Param param = new CreateSubjectUseCase.Param(kit.getId(),
            index,
            title,
            description,
            weight,
            currentUserId);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);
        when(createSubjectPort.persist(any(CreateSubjectPort.Param.class))).thenReturn(subscriberId);

        long actualSubject = createSubjectService.createSubject(param);
        assertEquals(subscriberId, actualSubject);
    }
}
