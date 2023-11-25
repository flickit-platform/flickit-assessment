package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.SneakyThrows;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.domain.MaturityLevelCompetence;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.out.assessmentresult.InvalidateAssessmentResultByKitPort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.CreateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.DeleteLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.UpdateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.CreateMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.DeleteMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelByCodePort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.UpdateMaturityLevelPort;
import org.flickit.assessment.kit.application.service.DslTranslator;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MaturityLevelUpdateKitPersisterTest {

    public static final String FILE = "src/test/resources/dsl.json";
    @InjectMocks
    private MaturityLevelUpdateKitPersister persister;
    @Mock
    private CreateMaturityLevelPort createMaturityLevelPort;
    @Mock
    private DeleteMaturityLevelPort deleteMaturityLevelPort;
    @Mock
    private LoadMaturityLevelByCodePort loadMaturityLevelByCodePort;
    @Mock
    private UpdateMaturityLevelPort updateMaturityLevelPort;
    @Mock
    private DeleteLevelCompetencePort deleteLevelCompetencePort;
    @Mock
    private CreateLevelCompetencePort createLevelCompetencePort;
    @Mock
    private UpdateLevelCompetencePort updateLevelCompetencePort;

    @Mock
    private InvalidateAssessmentResultByKitPort invalidateAssessmentResultByKitPort;

    @Test
    @SneakyThrows
    void testMaturityLevelUpdateKitPersister_SameInputsAsDatabaseData_NoChange() {
        Long kitId = 1L;
        AssessmentKit savedKit = AssessmentKitMother.kitWithFiveLevels(kitId);

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        AssessmentKitDslModel dslKit = DslTranslator.parseJson(dslContent);
        persister.persist(savedKit, dslKit);

        verifyNoInteractions(
            deleteMaturityLevelPort,
            deleteLevelCompetencePort,
            createMaturityLevelPort,
            createLevelCompetencePort,
            loadMaturityLevelByCodePort,
            updateMaturityLevelPort,
            updateLevelCompetencePort);
    }

    @Test
    @SneakyThrows
    void testMaturityLevelUpdateKitPersister_MaturityLevelAdded_AddToDatabase() {
        Long kitId = 1L;
        AssessmentKit savedKit = AssessmentKitMother.kitWithFourLevels(kitId);
        when(loadMaturityLevelByCodePort.loadByCode(LEVEL_TWO_CODE, kitId)).thenReturn(MaturityLevelMother.levelTwo());
        when(loadMaturityLevelByCodePort.loadByCode(LEVEL_THREE_CODE, kitId)).thenReturn(MaturityLevelMother.levelThree());
        when(loadMaturityLevelByCodePort.loadByCode(LEVEL_FOUR_CODE, kitId)).thenReturn(MaturityLevelMother.levelFour());
        MaturityLevel levelFive = levelFive(5);
        when(loadMaturityLevelByCodePort.loadByCode(LEVEL_FIVE_CODE, kitId)).thenReturn(levelFive);
        Long persistedLevelId = levelFive.getId();
        when(createMaturityLevelPort.persist(any(MaturityLevel.class), eq(kitId))).thenReturn(persistedLevelId);
        levelFive.getCompetences().forEach(i ->
            when(createLevelCompetencePort.persist(persistedLevelId, i.getEffectiveLevelId(), i.getValue())).thenReturn(1L));
        doNothing().when(invalidateAssessmentResultByKitPort).invalidateByKitId(kitId);

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        AssessmentKitDslModel dslKit = DslTranslator.parseJson(dslContent);
        persister.persist(savedKit, dslKit);

        verify(loadMaturityLevelByCodePort, times(4)).loadByCode(anyString(), anyLong());
        verify(createMaturityLevelPort, times(1)).persist(any(MaturityLevel.class), eq(kitId));
        verify(invalidateAssessmentResultByKitPort, times(1))
            .invalidateByKitId(kitId);
        verifyNoInteractions(
            deleteMaturityLevelPort,
            deleteLevelCompetencePort,
            updateMaturityLevelPort,
            updateLevelCompetencePort);
    }

    @Test
    @SneakyThrows
    void testMaturityLevelUpdateKitPersister_MaturityLevelDeleted_DeleteFromDatabase() {
        Long kitId = 1L;
        AssessmentKit savedKit = AssessmentKitMother.kitWithSixLevels(kitId);
        doNothing().when(deleteMaturityLevelPort).delete(LEVEL_SIX_ID);
        doNothing().when(deleteLevelCompetencePort).delete(LEVEL_SIX_ID, LEVEL_TWO_ID);
        doNothing().when(deleteLevelCompetencePort).delete(LEVEL_SIX_ID, LEVEL_THREE_ID);
        doNothing().when(deleteLevelCompetencePort).delete(LEVEL_SIX_ID, LEVEL_FOUR_ID);
        doNothing().when(deleteLevelCompetencePort).delete(LEVEL_SIX_ID, LEVEL_FIVE_ID);
        doNothing().when(deleteLevelCompetencePort).delete(LEVEL_SIX_ID, LEVEL_SIX_ID);
        doNothing().when(invalidateAssessmentResultByKitPort).invalidateByKitId(kitId);

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        AssessmentKitDslModel dslKit = DslTranslator.parseJson(dslContent);
        persister.persist(savedKit, dslKit);

        verify(deleteMaturityLevelPort, times(1)).delete(LEVEL_SIX_ID);
        verify(deleteLevelCompetencePort, times(5)).delete(any(), any());
        verify(invalidateAssessmentResultByKitPort, times(1))
            .invalidateByKitId(kitId);
        verifyNoInteractions(
            createMaturityLevelPort,
            createLevelCompetencePort,
            loadMaturityLevelByCodePort,
            updateMaturityLevelPort,
            updateLevelCompetencePort);
    }

    @Test
    @SneakyThrows
    void testMaturityLevelUpdateKitPersister_MaturityLevelUpdated_UpdateInDatabase() {
        Long kitId = 1L;
        AssessmentKit savedKit = AssessmentKitMother.kitWithFiveLevelsWithLevelFiveValue(kitId, 6);
        var updateParam = new UpdateMaturityLevelPort.Param(LEVEL_FIVE_ID, LEVEL_FIVE_CODE, 5, 5);
        doNothing().when(updateMaturityLevelPort).update(updateParam);
        doNothing().when(invalidateAssessmentResultByKitPort).invalidateByKitId(kitId);

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        AssessmentKitDslModel dslKit = DslTranslator.parseJson(dslContent);
        persister.persist(savedKit, dslKit);

        ArgumentCaptor<UpdateMaturityLevelPort.Param> updateCaptor = ArgumentCaptor.forClass(UpdateMaturityLevelPort.Param.class);
        verify(updateMaturityLevelPort).update(updateCaptor.capture());

        assertEquals(updateParam.id(), updateCaptor.getValue().id());
        assertEquals(updateParam.title(), updateCaptor.getValue().title());
        assertEquals(updateParam.index(), updateCaptor.getValue().index());
        assertEquals(updateParam.index(), updateCaptor.getValue().index());
        assertEquals(updateParam.value(), updateCaptor.getValue().value());

        verify(invalidateAssessmentResultByKitPort, times(1)).invalidateByKitId(kitId);
        verifyNoInteractions(
            createMaturityLevelPort,
            createLevelCompetencePort,
            loadMaturityLevelByCodePort,
            deleteMaturityLevelPort,
            deleteLevelCompetencePort,
            updateLevelCompetencePort);
    }

    @Test
    @SneakyThrows
    void testMaturityLevelUpdateKitPersister_LevelCompetenceAdded_AddToDatabase() {
        Long kitId = 1L;
        AssessmentKit savedKit = AssessmentKitMother.kitWithFiveLevels(kitId);
        var newCompetence = savedKit.getMaturityLevels().get(4).getCompetences().get(0);
        savedKit.getMaturityLevels().get(4).getCompetences().remove(0);
        when(loadMaturityLevelByCodePort.loadByCode(LEVEL_TWO_CODE, kitId)).thenReturn(MaturityLevelMother.levelTwo());
        when(createLevelCompetencePort.persist(LEVEL_FIVE_ID, newCompetence.getEffectiveLevelId(), newCompetence.getValue()))
            .thenReturn(1L);
        doNothing().when(invalidateAssessmentResultByKitPort).invalidateByKitId(kitId);

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        AssessmentKitDslModel dslKit = DslTranslator.parseJson(dslContent);
        persister.persist(savedKit, dslKit);

        verify(loadMaturityLevelByCodePort, times(1)).loadByCode(LEVEL_TWO_CODE, kitId);
        verify(createLevelCompetencePort, times(1))
            .persist(LEVEL_FIVE_ID, newCompetence.getEffectiveLevelId(), newCompetence.getValue());
        verify(invalidateAssessmentResultByKitPort, times(1))
            .invalidateByKitId(kitId);
        verifyNoInteractions(
            deleteMaturityLevelPort,
            deleteLevelCompetencePort,
            createMaturityLevelPort,
            updateMaturityLevelPort,
            updateLevelCompetencePort);
    }

    @Test
    @SneakyThrows
    void testMaturityLevelUpdateKitPersister_LevelCompetenceDeleted_AddToDatabase() {
        Long kitId = 1L;
        AssessmentKit savedKit = AssessmentKitMother.kitWithFiveLevels(kitId);
        savedKit.getMaturityLevels().get(4).getCompetences().add(new MaturityLevelCompetence(LEVEL_ONE_ID, LEVEL_ONE_CODE, 100));
        when(loadMaturityLevelByCodePort.loadByCode(LEVEL_ONE_CODE, kitId)).thenReturn(MaturityLevelMother.levelOne());
        doNothing().when(deleteLevelCompetencePort).delete(LEVEL_FIVE_ID, LEVEL_ONE_ID);
        doNothing().when(invalidateAssessmentResultByKitPort).invalidateByKitId(kitId);

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        AssessmentKitDslModel dslKit = DslTranslator.parseJson(dslContent);
        persister.persist(savedKit, dslKit);

        verify(loadMaturityLevelByCodePort, times(1)).loadByCode(anyString(), anyLong());
        verify(deleteLevelCompetencePort, times(1)).delete(LEVEL_FIVE_ID, LEVEL_ONE_ID);
        verify(invalidateAssessmentResultByKitPort, times(1))
            .invalidateByKitId(kitId);
        verifyNoInteractions(
            createMaturityLevelPort,
            createLevelCompetencePort,
            deleteMaturityLevelPort,
            updateMaturityLevelPort,
            updateLevelCompetencePort);
    }

    @Test
    @SneakyThrows
    void testMaturityLevelUpdateKitPersister_LevelCompetenceUpdated_UpdateInDatabase() {
        Long kitId = 1L;
        AssessmentKit savedKit = AssessmentKitMother.kitWithFiveLevels(kitId);
        savedKit.getMaturityLevels().get(1).getCompetences().remove(0);
        savedKit.getMaturityLevels().get(1).getCompetences().add(new MaturityLevelCompetence(LEVEL_TWO_ID, LEVEL_TWO_CODE, 70));
        when(loadMaturityLevelByCodePort.loadByCode(LEVEL_TWO_CODE, kitId)).thenReturn(MaturityLevelMother.levelTwo());
        doNothing().when(updateLevelCompetencePort).update(LEVEL_TWO_ID, LEVEL_TWO_ID, 60);
        doNothing().when(invalidateAssessmentResultByKitPort).invalidateByKitId(kitId);

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        AssessmentKitDslModel dslKit = DslTranslator.parseJson(dslContent);
        persister.persist(savedKit, dslKit);

        verify(loadMaturityLevelByCodePort, times(1)).loadByCode(LEVEL_TWO_CODE, kitId);
        verify(updateLevelCompetencePort, times(1)).update(LEVEL_TWO_ID, LEVEL_TWO_ID, 60);
        verify(invalidateAssessmentResultByKitPort, times(1))
            .invalidateByKitId(kitId);
        verifyNoInteractions(
            createMaturityLevelPort,
            createLevelCompetencePort,
            deleteMaturityLevelPort,
            deleteLevelCompetencePort,
            updateMaturityLevelPort);
    }
}
