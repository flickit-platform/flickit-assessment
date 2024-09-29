package org.flickit.assessment.kit.application.service.attribute;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.port.in.attribute.CreateAttributeUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.attribute.CreateAttributePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateAttributeServiceTest {

    @InjectMocks
    private CreateAttributeService service;

    @Mock
    private CreateAttributePort createAttributePort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    private final UUID ownerId = UUID.randomUUID();
    private final AssessmentKit kit = AssessmentKitMother.simpleKit();

    @Test
    void testCreateAttribute_WhenCurrentUserIsNotOwner_ShouldThrowAccessDeniedException() {
        var param = createParam(CreateAttributeUseCase.Param.ParamBuilder::build);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAttribute(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testCreateAttribute_WhenCurrentUserIsOwner_ThenCreateAttribute() {
        long attributeId = 123L;
        var param = createParam(b -> b.currentUserId(ownerId));

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);
        when(createAttributePort.persist(any(Attribute.class), anyLong(), anyLong())).thenReturn(attributeId);

        long actualAttributeId = service.createAttribute(param);

        assertEquals(attributeId, actualAttributeId);
    }

    private CreateAttributeUseCase.Param createParam(Consumer<CreateAttributeUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateAttributeUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAttributeUseCase.Param.builder()
            .kitId(1L)
            .index(1)
            .title("software maintainability")
            .description("desc")
            .weight(2)
            .subjectId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
