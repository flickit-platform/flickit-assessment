package org.flickit.assessment.kit.application.service.subject;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectByWizardUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectByWizardPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateSubjectByWizardServiceTest {

    @InjectMocks
    private UpdateSubjectByWizardService service;

    @Mock
    private UpdateSubjectByWizardPort updateSubjectByWizardPort;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    private int index;
    private String title;
    private String description;
    private int weight;
    private UUID currentUserId;
    private UUID ownerId;
    private AssessmentKit kit;

    @BeforeEach
    void setUp() {
        kit = AssessmentKitMother.simpleKit();
        index = 2;
        title = "team";
        description = "about team";
        weight = 3;
        currentUserId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
    }

    @Test
    void testUpdateSubjectByWizard_WhenCurrentUserIsNotOwner_ShouldThrowAccessDeniedException() {
        UpdateSubjectByWizardUseCase.Param param = new UpdateSubjectByWizardUseCase.Param(kit.getId(),
            kit.getActiveVersionId(),
            index,
            title,
            description,
            weight,
            currentUserId);

        when(loadAssessmentKitPort.load(kit.getId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);

        assertThrows(AccessDeniedException.class, () -> service.updateSubject(param));
    }

    @Test
    void testUpdateSubjectByWizard_WhenCurrentUserIsOwner_ShouldUpdateSubject() {
        currentUserId = ownerId;
        UpdateSubjectByWizardUseCase.Param param = new UpdateSubjectByWizardUseCase.Param(kit.getId(),
            kit.getActiveVersionId(),
            index,
            title,
            description,
            weight,
            currentUserId);

        when(loadAssessmentKitPort.load(kit.getId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);
        service.updateSubject(param);
        verify(updateSubjectByWizardPort).updateByWizard(any(UpdateSubjectByWizardPort.Param.class));
    }
}
