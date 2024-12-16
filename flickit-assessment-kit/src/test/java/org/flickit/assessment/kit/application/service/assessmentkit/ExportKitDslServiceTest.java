package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.port.in.assessmentkit.ExportKitDslUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadActiveKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExportKitDslServiceTest {

    @InjectMocks
    private ExportKitDslService service;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    @Mock
    private LoadActiveKitVersionIdPort loadActiveKitVersionIdPort;

    @Mock
    private LoadSubjectsPort loadSubjectsPort;

    @Mock
    private LoadAttributesPort loadAttributesPort;

    @Mock
    LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    private final AssessmentKit kit = AssessmentKitMother.simpleKit();

    @Test
    void testExportKitDsl_userIsNotExpertGroupOwner_throwsAccessDeniedException() {
        var param = createParam(ExportKitDslUseCase.Param.ParamBuilder::build);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.export(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testExportKitDsl_activeKitVersionNotFound_throwsAccessDeniedException() {
        var param = createParam(ExportKitDslUseCase.Param.ParamBuilder::build);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(param.getCurrentUserId());

        assertThrows(AccessDeniedException.class, () -> service.export(param));
    }

    private ExportKitDslUseCase.Param createParam(Consumer<ExportKitDslUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private ExportKitDslUseCase.Param.ParamBuilder paramBuilder() {
        return ExportKitDslUseCase.Param.builder()
            .kitId(123L)
            .currentUserId(UUID.randomUUID());
    }

}
