package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.SneakyThrows;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UpdateKitByDslUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitInfoPort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.CompositeUpdateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterResult;
import org.flickit.assessment.kit.application.service.assessmentkit.validate.CompositeUpdateKitValidator;
import org.flickit.assessment.kit.common.Notification;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateKitByDslServiceTest {

    public static final String FILE = "src/test/resources/dsl.json";

    @InjectMocks
    private UpdateKitByDslService service;

    @Mock
    private LoadAssessmentKitInfoPort loadAssessmentKitInfoPort;

    @Mock
    private CompositeUpdateKitValidator validator;

    @Mock
    private CompositeUpdateKitPersister persister;


    @Test
    @SneakyThrows
    void testUpdateKitByDsl_SameInputsAsDatabaseData_NoChange() {
        Long kitId = 1L;
        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        AssessmentKit savedKit = AssessmentKitMother.kitWithFiveLevels(kitId);
        when(loadAssessmentKitInfoPort.load(kitId)).thenReturn(savedKit);
        when(validator.validate(any(AssessmentKit.class), any(AssessmentKitDslModel.class))).thenReturn(new Notification());
        when(persister.persist(any(AssessmentKit.class), any(AssessmentKitDslModel.class))).thenReturn(new UpdateKitPersisterResult(false));

        var param = new UpdateKitByDslUseCase.Param(kitId, dslContent);
        service.update(param);

        verify(loadAssessmentKitInfoPort, times(1)).load(kitId);
    }

}
