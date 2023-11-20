package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.SneakyThrows;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.domain.MaturityLevelCompetence;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UpdateKitByDslUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitInfoPort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.CreateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.DeleteLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.UpdateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.CreateMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.DeleteMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelByTitlePort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.UpdateMaturityLevelPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateKitByDslServiceTest {

    public static final String FILE = "src/test/resources/dsl.json";

    @InjectMocks
    private UpdateKitByDslService service;

    @Mock
    private LoadAssessmentKitInfoPort loadAssessmentKitInfoPort;

    @Mock
    private CreateMaturityLevelPort createMaturityLevelPort;

    @Mock
    private DeleteMaturityLevelPort deleteMaturityLevelPort;

    @Mock
    private LoadMaturityLevelByTitlePort loadMaturityLevelByTitlePort;

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
        when(loadAssessmentKitInfoPort.load(kitId)).thenReturn(AssessmentKitMother.kitWithFiveLevels(kitId));
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_TWO_CODE, kitId)).thenReturn(MaturityLevelMother.levelTwo());
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_THREE_CODE, kitId)).thenReturn(MaturityLevelMother.levelThree());
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_FOUR_CODE, kitId)).thenReturn(MaturityLevelMother.levelFour());
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_FIVE_CODE, kitId)).thenReturn(MaturityLevelMother.levelFive(5));

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        var param = new UpdateKitByDslUseCase.Param(kitId, dslContent);
        service.update(param);

        verify(loadAssessmentKitInfoPort, times(1)).load(kitId);
        verify(loadMaturityLevelByTitlePort, times(10)).loadByTitle(anyString(), anyLong());
    }

    @Test
    @SneakyThrows
    void testUpdateKitByDsl_MaturityLevelAdded_AddToDatabase() {
        Long kitId = 1L;
        when(loadAssessmentKitInfoPort.load(kitId)).thenReturn(AssessmentKitMother.kitWithFourLevels(kitId));
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_TWO_CODE, kitId)).thenReturn(MaturityLevelMother.levelTwo());
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_THREE_CODE, kitId)).thenReturn(MaturityLevelMother.levelThree());
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_FOUR_CODE, kitId)).thenReturn(MaturityLevelMother.levelFour());
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_FIVE_CODE, kitId)).thenReturn(MaturityLevelMother.levelFive(5));
        doNothing().when(createMaturityLevelPort).persist(any(MaturityLevel.class), eq(kitId));

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        var param = new UpdateKitByDslUseCase.Param(kitId, dslContent);
        service.update(param);

        verify(loadAssessmentKitInfoPort, times(1)).load(kitId);
        verify(loadMaturityLevelByTitlePort, times(10)).loadByTitle(anyString(), anyLong());
        verify(createMaturityLevelPort, times(1)).persist(any(MaturityLevel.class), eq(kitId));
        verifyNoInteractions(
            deleteMaturityLevelPort,
            deleteLevelCompetencePort,
            createLevelCompetencePort,
            updateMaturityLevelPort,
            updateLevelCompetencePort);
    }

    @Test
    @SneakyThrows
    void testUpdateKitByDsl_MaturityLevelDeleted_DeleteFromDatabase() {
        Long kitId = 1L;
        when(loadAssessmentKitInfoPort.load(kitId)).thenReturn(AssessmentKitMother.kitWithSixLevels(kitId));
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_TWO_CODE, kitId)).thenReturn(MaturityLevelMother.levelTwo());
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_THREE_CODE, kitId)).thenReturn(MaturityLevelMother.levelThree());
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_FOUR_CODE, kitId)).thenReturn(MaturityLevelMother.levelFour());
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_FIVE_CODE, kitId)).thenReturn(MaturityLevelMother.levelFive(5));
        doNothing().when(deleteMaturityLevelPort).delete(LEVEL_SIX_ID);
        doNothing().when(deleteLevelCompetencePort).delete(LEVEL_TWO_ID, LEVEL_SIX_ID, kitId);
        doNothing().when(deleteLevelCompetencePort).delete(LEVEL_THREE_ID, LEVEL_SIX_ID, kitId);
        doNothing().when(deleteLevelCompetencePort).delete(LEVEL_FOUR_ID, LEVEL_SIX_ID, kitId);
        doNothing().when(deleteLevelCompetencePort).delete(LEVEL_FIVE_ID, LEVEL_SIX_ID, kitId);
        doNothing().when(deleteLevelCompetencePort).delete(LEVEL_SIX_ID, LEVEL_SIX_ID, kitId);

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        var param = new UpdateKitByDslUseCase.Param(kitId, dslContent);
        service.update(param);

        verify(loadAssessmentKitInfoPort, times(1)).load(kitId);
        verify(loadMaturityLevelByTitlePort, times(10)).loadByTitle(anyString(), anyLong());
        verify(deleteMaturityLevelPort, times(1)).delete(LEVEL_SIX_ID);
        verify(deleteLevelCompetencePort, times(5)).delete(any(), any(), eq(kitId));
        verifyNoInteractions(createMaturityLevelPort, createLevelCompetencePort, updateMaturityLevelPort, updateLevelCompetencePort);
    }

    @Test
    @SneakyThrows
    void testUpdateKitByDsl_MaturityLevelUpdated_UpdateInDatabase() {
        Long kitId = 1L;
        when(loadAssessmentKitInfoPort.load(kitId)).thenReturn(AssessmentKitMother.kitWithFiveLevelsWithLevelFiveValue(kitId, 6));
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_TWO_CODE, kitId)).thenReturn(MaturityLevelMother.levelTwo());
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_THREE_CODE, kitId)).thenReturn(MaturityLevelMother.levelThree());
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_FOUR_CODE, kitId)).thenReturn(MaturityLevelMother.levelFour());
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_FIVE_CODE, kitId)).thenReturn(MaturityLevelMother.levelFive(6));
        var updateParam = new UpdateMaturityLevelPort.Param(kitId, LEVEL_FIVE_CODE, LEVEL_FIVE_CODE, 5, 5);
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
        verify(loadMaturityLevelByTitlePort, times(10)).loadByTitle(anyString(), anyLong());
        verifyNoInteractions(
            createMaturityLevelPort,
            createLevelCompetencePort,
            deleteMaturityLevelPort,
            deleteLevelCompetencePort,
            updateLevelCompetencePort);
    }

    @Test
    @SneakyThrows
    void testUpdateKitByDsl_LevelCompetenceAdded_AddToDatabase() {
        Long kitId = 1L;
        AssessmentKit assessmentKit = AssessmentKitMother.kitWithFiveLevels(kitId);
        var newCompetence = assessmentKit.getMaturityLevels().get(4).getCompetences().get(0);
        assessmentKit.getMaturityLevels().get(4).getCompetences().remove(0);
        when(loadAssessmentKitInfoPort.load(kitId)).thenReturn(assessmentKit);
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_TWO_CODE, kitId)).thenReturn(MaturityLevelMother.levelTwo());
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_THREE_CODE, kitId)).thenReturn(MaturityLevelMother.levelThree());
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_FOUR_CODE, kitId)).thenReturn(MaturityLevelMother.levelFour());
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_FIVE_CODE, kitId)).thenReturn(MaturityLevelMother.levelFive(5));
        when(createLevelCompetencePort.persist(newCompetence.getEffectiveLevelId(), newCompetence.getValue(), LEVEL_FIVE_CODE, kitId))
            .thenReturn(anyLong());

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        var param = new UpdateKitByDslUseCase.Param(kitId, dslContent);
        service.update(param);

        verify(loadAssessmentKitInfoPort, times(1)).load(kitId);
        verify(loadMaturityLevelByTitlePort, times(10)).loadByTitle(anyString(), anyLong());
        verify(createLevelCompetencePort, times(1))
            .persist(newCompetence.getEffectiveLevelId(), newCompetence.getValue(), LEVEL_FIVE_CODE, kitId);
        verifyNoInteractions(
            deleteMaturityLevelPort,
            deleteLevelCompetencePort,
            createMaturityLevelPort,
            updateMaturityLevelPort,
            updateLevelCompetencePort);
    }

    @Test
    @SneakyThrows
    void testUpdateKitByDsl_LevelCompetenceDeleted_AddToDatabase() {
        Long kitId = 1L;
        AssessmentKit assessmentKit = AssessmentKitMother.kitWithFiveLevels(kitId);
        assessmentKit.getMaturityLevels().get(4).getCompetences().add(new MaturityLevelCompetence(LEVEL_ONE_ID, 100));
        when(loadAssessmentKitInfoPort.load(kitId)).thenReturn(assessmentKit);
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_TWO_CODE, kitId)).thenReturn(MaturityLevelMother.levelTwo());
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_THREE_CODE, kitId)).thenReturn(MaturityLevelMother.levelThree());
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_FOUR_CODE, kitId)).thenReturn(MaturityLevelMother.levelFour());
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_FIVE_CODE, kitId)).thenReturn(MaturityLevelMother.levelFive(5));
        doNothing().when(deleteLevelCompetencePort).delete(LEVEL_ONE_ID, LEVEL_FIVE_ID, kitId);

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        var param = new UpdateKitByDslUseCase.Param(kitId, dslContent);
        service.update(param);

        verify(loadAssessmentKitInfoPort, times(1)).load(kitId);
        verify(loadMaturityLevelByTitlePort, times(10)).loadByTitle(anyString(), anyLong());
        verify(deleteLevelCompetencePort, times(1)).delete(LEVEL_ONE_ID, LEVEL_FIVE_ID, kitId);
        verifyNoInteractions(
            createMaturityLevelPort,
            createLevelCompetencePort,
            deleteMaturityLevelPort,
            updateMaturityLevelPort,
            updateLevelCompetencePort);
    }

    @Test
    @SneakyThrows
    void testUpdateKitByDsl_LevelCompetenceUpdated_UpdateInDatabase() {
        Long kitId = 1L;
        AssessmentKit assessmentKit = AssessmentKitMother.kitWithFiveLevels(kitId);
        assessmentKit.getMaturityLevels().get(4).getCompetences().add(new MaturityLevelCompetence(LEVEL_TWO_ID, 100));
        when(loadAssessmentKitInfoPort.load(kitId)).thenReturn(assessmentKit);
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_TWO_CODE, kitId)).thenReturn(MaturityLevelMother.levelTwo());
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_THREE_CODE, kitId)).thenReturn(MaturityLevelMother.levelThree());
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_FOUR_CODE, kitId)).thenReturn(MaturityLevelMother.levelFour());
        when(loadMaturityLevelByTitlePort.loadByTitle(LEVEL_FIVE_CODE, kitId)).thenReturn(MaturityLevelMother.levelFive(5));
        doNothing().when(updateLevelCompetencePort).update(LEVEL_FIVE_ID, LEVEL_TWO_ID, 95, kitId);

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        var param = new UpdateKitByDslUseCase.Param(kitId, dslContent);
        service.update(param);

        verify(loadAssessmentKitInfoPort, times(1)).load(kitId);
        verify(loadMaturityLevelByTitlePort, times(10)).loadByTitle(anyString(), anyLong());
        verify(updateLevelCompetencePort, times(1)).update(LEVEL_FIVE_ID, LEVEL_TWO_ID, 95, kitId);
        verifyNoInteractions(
            createMaturityLevelPort,
            createLevelCompetencePort,
            deleteMaturityLevelPort,
            deleteLevelCompetencePort,
            updateMaturityLevelPort);
    }
}
