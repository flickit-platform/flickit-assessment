package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.SneakyThrows;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.exception.NotValidMaturityLevelException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UpdateKitByDslUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitInfoPort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.CreateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.DeleteLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.UpdateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.CreateMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.DeleteMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.UpdateMaturityLevelPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_KIT_BY_DSL_MATURITY_LEVEL_NOT_VALID;
import static org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateKitByDslServiceTest {

    public static final String FILE = "src/test/resources/dsl.json";
    public static final String ERROR_FILE = "src/test/resources/dsl_with_error.json";

    @InjectMocks
    private UpdateKitByDslService service;

    @Mock
    private LoadAssessmentKitInfoPort loadAssessmentKitInfoPort;

    @Mock
    private CreateMaturityLevelPort createMaturityLevelPort;

    @Mock
    private DeleteMaturityLevelPort deleteMaturityLevelPort;

    @Mock
    private UpdateMaturityLevelPort updateMaturityLevelPort;

    @Mock
    private DeleteLevelCompetencePort deleteLevelCompetencePort;

    @Mock
    private CreateLevelCompetencePort createLevelCompetencePort;

    @Mock
    private UpdateLevelCompetencePort updateLevelCompetencePort;

    @Test
    @SneakyThrows
    void testUpdateKitByDsl_SameInputsAsDatabaseData_NoChange() {
        Long kitId = 1L;
        when(loadAssessmentKitInfoPort.load(kitId)).thenReturn(AssessmentKitMother.kitWithFiveLevels());

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        var param = new UpdateKitByDslUseCase.Param(kitId, dslContent);
        service.update(param);

        verify(loadAssessmentKitInfoPort, times(1)).load(kitId);
    }

    @Test
    @SneakyThrows
    void testUpdateKitByDsl_MaturityLevelAdded_AddToDatabase() {
        Long kitId = 1L;
        when(loadAssessmentKitInfoPort.load(kitId)).thenReturn(AssessmentKitMother.kitWithFourLevels());
        doNothing().when(createMaturityLevelPort).persist(any(MaturityLevel.class), eq(kitId));

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        var param = new UpdateKitByDslUseCase.Param(kitId, dslContent);
        service.update(param);

        verify(loadAssessmentKitInfoPort, times(1)).load(kitId);
        verify(createMaturityLevelPort, times(1)).persist(any(MaturityLevel.class), eq(kitId));
        verifyNoInteractions(deleteMaturityLevelPort, deleteLevelCompetencePort, createLevelCompetencePort, updateMaturityLevelPort, updateLevelCompetencePort);
    }

    @Test
    @SneakyThrows
    void testUpdateKitByDsl_MaturityLevelDeleted_DeleteFromDatabase() {
        Long kitId = 1L;
        when(loadAssessmentKitInfoPort.load(kitId)).thenReturn(AssessmentKitMother.kitWithSixLevels());
        doNothing().when(deleteMaturityLevelPort).delete(LEVEL_SIX_ID);
        doNothing().when(deleteLevelCompetencePort).delete(LEVEL_TWO_CODE, LEVEL_SIX_ID, kitId);
        doNothing().when(deleteLevelCompetencePort).delete(LEVEL_THREE_CODE, LEVEL_SIX_ID, kitId);
        doNothing().when(deleteLevelCompetencePort).delete(LEVEL_FOUR_CODE, LEVEL_SIX_ID, kitId);
        doNothing().when(deleteLevelCompetencePort).delete(LEVEL_FIVE_CODE, LEVEL_SIX_ID, kitId);
        doNothing().when(deleteLevelCompetencePort).delete(LEVEL_SIX_CODE, LEVEL_SIX_ID, kitId);

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        var param = new UpdateKitByDslUseCase.Param(kitId, dslContent);
        service.update(param);

        verify(loadAssessmentKitInfoPort, times(1)).load(kitId);
        verify(deleteMaturityLevelPort, times(1)).delete(LEVEL_SIX_ID);
        verify(deleteLevelCompetencePort, times(5)).delete(any(), any(), eq(kitId));
        verifyNoInteractions(createMaturityLevelPort, createLevelCompetencePort, updateMaturityLevelPort, updateLevelCompetencePort);
    }

    @Test
    @SneakyThrows
    void testUpdateKitByDsl_MaturityLevelUpdated_UpdateInDatabase() {
        Long kitId = 1L;
        when(loadAssessmentKitInfoPort.load(kitId)).thenReturn(AssessmentKitMother.kitWithFiveLevelsWithLevelFiveValue(6));
        var updateParam = new UpdateMaturityLevelPort.Param(LEVEL_FIVE_CODE, LEVEL_FIVE_CODE, 5, 5);
        doNothing().when(updateMaturityLevelPort).update(updateParam);

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        var param = new UpdateKitByDslUseCase.Param(kitId, dslContent);
        service.update(param);

        ArgumentCaptor<UpdateMaturityLevelPort.Param> updateCaptor = ArgumentCaptor.forClass(UpdateMaturityLevelPort.Param.class);
        verify(updateMaturityLevelPort).update(updateCaptor.capture());

        assertEquals(updateParam.code(), updateCaptor.getValue().code());
        assertEquals(updateParam.title(), updateCaptor.getValue().title());
        assertEquals(updateParam.index(), updateCaptor.getValue().index());
        assertEquals(updateParam.index(), updateCaptor.getValue().index());
        assertEquals(updateParam.value(), updateCaptor.getValue().value());

        verify(loadAssessmentKitInfoPort, times(1)).load(kitId);
        verifyNoInteractions(createMaturityLevelPort, createLevelCompetencePort, deleteMaturityLevelPort, deleteLevelCompetencePort, updateLevelCompetencePort);
    }

    @Test
    @SneakyThrows
    void testUpdateKitByDsl_MaturityLevelUpdatedToExistingValue_ErrorMessage() {
        Long kitId = 1L;
        when(loadAssessmentKitInfoPort.load(kitId)).thenReturn(AssessmentKitMother.kitWithFiveLevels());

        String dslContent = new String(Files.readAllBytes(Paths.get(ERROR_FILE)));
        var param = new UpdateKitByDslUseCase.Param(kitId, dslContent);
        var throwable = assertThrows(NotValidMaturityLevelException.class, () -> service.update(param));
        assertThat(throwable).hasMessage(UPDATE_KIT_BY_DSL_MATURITY_LEVEL_NOT_VALID);

        verify(loadAssessmentKitInfoPort, times(1)).load(kitId);
        verifyNoInteractions(createMaturityLevelPort,
            createLevelCompetencePort,
            deleteMaturityLevelPort,
            deleteLevelCompetencePort,
            updateMaturityLevelPort,
            updateLevelCompetencePort);
    }

    @Test
    @SneakyThrows
    void testUpdateKitByDslLevelCompetenceAdded_AddToDatabase() {
        Long kitId = 1L;
        AssessmentKit assessmentKit = AssessmentKitMother.kitWithFiveLevels();
        assessmentKit.getMaturityLevels().get(4).getLevelCompetence().remove(LEVEL_TWO_CODE);
        when(loadAssessmentKitInfoPort.load(kitId)).thenReturn(assessmentKit);
        when(createLevelCompetencePort.persist(LEVEL_TWO_CODE, 95, LEVEL_FIVE_CODE, kitId)).thenReturn(anyLong());

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        var param = new UpdateKitByDslUseCase.Param(kitId, dslContent);
        service.update(param);

        verify(loadAssessmentKitInfoPort, times(1)).load(kitId);
        verify(createLevelCompetencePort, times(1)).persist(LEVEL_TWO_CODE, 95, LEVEL_FIVE_CODE, kitId);
        verifyNoInteractions(deleteMaturityLevelPort, deleteLevelCompetencePort, createMaturityLevelPort, updateMaturityLevelPort, updateLevelCompetencePort);
    }

    @Test
    @SneakyThrows
    void testUpdateKitByDslLevelCompetenceDeleted_AddToDatabase() {
        Long kitId = 1L;
        AssessmentKit assessmentKit = AssessmentKitMother.kitWithFiveLevels();
        assessmentKit.getMaturityLevels().get(4).getLevelCompetence().put(LEVEL_ONE_CODE, 100);
        when(loadAssessmentKitInfoPort.load(kitId)).thenReturn(assessmentKit);
        doNothing().when(deleteLevelCompetencePort).delete(LEVEL_ONE_CODE, LEVEL_FIVE_ID, kitId);

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        var param = new UpdateKitByDslUseCase.Param(kitId, dslContent);
        service.update(param);

        verify(loadAssessmentKitInfoPort, times(1)).load(kitId);
        verify(deleteLevelCompetencePort, times(1)).delete(LEVEL_ONE_CODE, LEVEL_FIVE_ID, kitId);
        verifyNoInteractions(createMaturityLevelPort, createLevelCompetencePort, deleteMaturityLevelPort, updateMaturityLevelPort, updateLevelCompetencePort);
    }

    @Test
    @SneakyThrows
    void testUpdateKitByDslLevelCompetenceUpdated_UpdateInDatabase() {
        Long kitId = 1L;
        AssessmentKit assessmentKit = AssessmentKitMother.kitWithFiveLevels();
        assessmentKit.getMaturityLevels().get(4).getLevelCompetence().put(LEVEL_TWO_CODE, 100);
        when(loadAssessmentKitInfoPort.load(kitId)).thenReturn(assessmentKit);
        doNothing().when(updateLevelCompetencePort).update(LEVEL_FIVE_ID, LEVEL_TWO_CODE, 95, kitId);

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        var param = new UpdateKitByDslUseCase.Param(kitId, dslContent);
        service.update(param);

        verify(loadAssessmentKitInfoPort, times(1)).load(kitId);
        verify(updateLevelCompetencePort, times(1)).update(LEVEL_FIVE_ID, LEVEL_TWO_CODE, 95, kitId);
        verifyNoInteractions(createMaturityLevelPort, createLevelCompetencePort, deleteMaturityLevelPort, deleteLevelCompetencePort, updateMaturityLevelPort);
    }
}
