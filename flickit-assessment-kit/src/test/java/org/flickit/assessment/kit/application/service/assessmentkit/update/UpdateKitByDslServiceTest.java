package org.flickit.assessment.kit.application.service.assessmentkit.update;

import lombok.SneakyThrows;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.common.exception.api.Notification;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UpdateKitByDslUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitInfoPort;
import org.flickit.assessment.kit.application.port.out.assessmentresult.InvalidateAssessmentResultByKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.validate.CompositeUpdateKitValidator;
import org.flickit.assessment.kit.application.service.assessmentkit.update.validate.impl.InvalidAdditionError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.COLLECTION;
import static org.flickit.assessment.kit.application.service.assessmentkit.update.validate.impl.DslFieldNames.SUBJECT;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateKitByDslServiceTest {

    public static final String FILE = "src/test/resources/dsl.json";

    @InjectMocks
    private UpdateKitByDslService service;
    @Mock
    private LoadAssessmentKitInfoPort loadAssessmentKitInfoPort;
    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    @Mock
    private InvalidateAssessmentResultByKitPort invalidateResultByKitPort;
    @Mock
    private CompositeUpdateKitValidator validator;
    @Mock
    private CompositeUpdateKitPersister persister;


    @Test
    @SneakyThrows
    void testUpdate_ValidChanges_NoNeedToInvalidateResult() {
        Long kitId = 1L;
        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        AssessmentKit savedKit = simpleKit();
        Optional<UUID> currentUserId = Optional.of(UUID.randomUUID());

        when(loadAssessmentKitInfoPort.load(kitId)).thenReturn(savedKit);
        when(loadExpertGroupOwnerPort.loadOwnerId(savedKit.getExpertGroupId())).thenReturn(currentUserId);
        when(validator.validate(any(AssessmentKit.class), any(AssessmentKitDslModel.class))).thenReturn(new Notification());
        when(persister.persist(any(AssessmentKit.class), any(AssessmentKitDslModel.class), any(UUID.class)))
            .thenReturn(new UpdateKitPersisterResult(false));

        var param = new UpdateKitByDslUseCase.Param(kitId, dslContent, currentUserId.get());
        service.update(param);
    }

    @Test
    @SneakyThrows
    void testUpdate_ValidChanges_NeedsToInvalidateResult() {
        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        AssessmentKit savedKit = simpleKit();
        Optional<UUID> currentUserId = Optional.of(UUID.randomUUID());

        when(loadAssessmentKitInfoPort.load(savedKit.getId())).thenReturn(savedKit);
        when(loadExpertGroupOwnerPort.loadOwnerId(savedKit.getExpertGroupId())).thenReturn(currentUserId);
        when(validator.validate(any(AssessmentKit.class), any(AssessmentKitDslModel.class))).thenReturn(new Notification());
        when(persister.persist(any(AssessmentKit.class), any(AssessmentKitDslModel.class), any(UUID.class))).thenReturn(new UpdateKitPersisterResult(true));
        doNothing().when(invalidateResultByKitPort).invalidateByKitId(savedKit.getId());

        var param = new UpdateKitByDslUseCase.Param(savedKit.getId(), dslContent, currentUserId.get());
        service.update(param);
    }

    @Test
    @SneakyThrows
    void testUpdate_InvalidChanges_ThrowException() {
        Long kitId = 1L;
        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        AssessmentKit savedKit = simpleKit();
        Optional<UUID> currentUserId = Optional.of(UUID.randomUUID());

        when(loadAssessmentKitInfoPort.load(kitId)).thenReturn(savedKit);
        when(loadExpertGroupOwnerPort.loadOwnerId(savedKit.getExpertGroupId())).thenReturn(currentUserId);

        Notification notification = new Notification();
        notification.add(new InvalidAdditionError(SUBJECT, Set.of("Team")));
        when(validator.validate(any(AssessmentKit.class), any(AssessmentKitDslModel.class))).thenReturn(notification);

        var param = new UpdateKitByDslUseCase.Param(kitId, dslContent, currentUserId.get());

        var throwable = assertThrows(ValidationException.class,
            () -> service.update(param));

        assertThat(throwable.getValidation())
            .returns(true, Notification::hasErrors)
            .extracting(Notification::getErrors, as(COLLECTION))
            .singleElement()
            .isInstanceOfSatisfying(InvalidAdditionError.class, x -> {
                assertThat(x.fieldName()).isEqualTo(SUBJECT);
                assertThat(x.addedItems()).contains("Team");
            });
    }


    @Test
    @SneakyThrows
    void testUpdate_UserIsNotExpertGroupOwner_ThrowException() {
        Long kitId = 1L;
        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        AssessmentKit savedKit = simpleKit();
        Optional<UUID> ownerId = Optional.of(UUID.randomUUID());
        UUID currentUserId = UUID.randomUUID();

        when(loadAssessmentKitInfoPort.load(kitId)).thenReturn(savedKit);
        when(loadExpertGroupOwnerPort.loadOwnerId(savedKit.getExpertGroupId())).thenReturn(ownerId);

        var param = new UpdateKitByDslUseCase.Param(kitId, dslContent, currentUserId);
        assertThrows(AccessDeniedException.class, () -> service.update(param));
    }
}
