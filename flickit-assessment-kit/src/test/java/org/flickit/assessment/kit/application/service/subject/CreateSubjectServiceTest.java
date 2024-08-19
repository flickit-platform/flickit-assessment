package org.flickit.assessment.kit.application.service.subject;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.port.in.subject.CreateSubjectUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.subject.CreateSubjectPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateSubjectServiceTest {

    @InjectMocks
    private CreateSubjectService service;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private CreateSubjectPort createSubjectPort;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    private final UUID ownerId = UUID.randomUUID();
    private final AssessmentKit kit = AssessmentKitMother.simpleKit();

    @Test
    void testCreateSubject_WhenCurrentUserIsNotOwner_ShouldThrowAccessDeniedException() {
        CreateSubjectUseCase.Param param = createParam(CreateSubjectUseCase.Param.ParamBuilder::build);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);

        assertThrows(AccessDeniedException.class, () -> service.createSubject(param));
    }

    @Test
    void testCreateSubject_WhenCurrentUserIsOwner_ShouldCreateSubject() {
        long subjectId = 1;
        CreateSubjectUseCase.Param param = createParam(b -> b.currentUserId(ownerId));

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);
        when(createSubjectPort.persist(any(CreateSubjectPort.Param.class))).thenReturn(subjectId);

        long actualSubject = service.createSubject(param);
        assertEquals(subjectId, actualSubject);
    }

    private CreateSubjectUseCase.Param createParam(Consumer<CreateSubjectUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateSubjectUseCase.Param.ParamBuilder paramBuilder() {
        return CreateSubjectUseCase.Param.builder()
            .kitId(kit.getId())
            .index(3)
            .title("Team")
            .description("team description")
            .weight(2)
            .currentUserId(UUID.randomUUID());
    }
}
