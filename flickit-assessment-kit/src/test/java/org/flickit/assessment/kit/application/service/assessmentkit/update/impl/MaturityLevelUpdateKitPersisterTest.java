package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.SneakyThrows;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.MaturityLevelDslModel;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.CreateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.DeleteLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.UpdateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.CreateMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.DeleteMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelByCodePort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.UpdateMaturityLevelPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.kitWithMaturityLevels;
import static org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother.*;
import static org.flickit.assessment.kit.test.fixture.application.dsl.MaturityLevelDslModelMother.competenceListToMap;
import static org.flickit.assessment.kit.test.fixture.application.dsl.MaturityLevelDslModelMother.domainToDslModel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaturityLevelUpdateKitPersisterTest {

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

    @Test
    void testOrder() {
        assertEquals(1, persister.order());
    }

    @Test
    void testPersist_ThreeSameLevelsInDbAndDsl_NoUpdate() {
        AssessmentKit savedKit = kitWithMaturityLevels(List.of(levelOne(), levelTwo(), levelThree()));

        MaturityLevelDslModel dslLevelOne = domainToDslModel(levelOne());
        MaturityLevelDslModel dslLevelTwo = domainToDslModel(levelTwo());
        MaturityLevelDslModel dslLevelThree = domainToDslModel(levelThree());

        List<MaturityLevelDslModel> dslLevels = List.of(dslLevelOne, dslLevelTwo, dslLevelThree);

        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .maturityLevels(dslLevels)
            .build();

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
    void testPersist_TwoSameLevelsInDbAnDsl_OneNewLevelInDsl_Create() {
        AssessmentKit savedKit = kitWithMaturityLevels(List.of(levelOne(), levelTwo()));

        MaturityLevelDslModel firstDslLevel = domainToDslModel(levelOne());
        MaturityLevelDslModel secondDslLevel = domainToDslModel(levelTwo());
        MaturityLevelDslModel thirdDslLevel = domainToDslModel(levelThree());

        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .maturityLevels(List.of(firstDslLevel, secondDslLevel, thirdDslLevel))
            .build();

        Long persistedLevelId = 3L;
        when(createMaturityLevelPort.persist(any(), eq(savedKit.getId()))).thenReturn(persistedLevelId);
        when(createLevelCompetencePort.persist(persistedLevelId, levelTwo().getId(), 75)).thenReturn(1L);
        when(createLevelCompetencePort.persist(persistedLevelId, levelThree().getId(), 60)).thenReturn(2L);
        when(loadMaturityLevelByCodePort.loadByCode(levelTwo().getCode(), savedKit.getId())).thenReturn(levelTwo());
        when(loadMaturityLevelByCodePort.loadByCode(levelThree().getCode(), savedKit.getId())).thenReturn(levelThree());

        persister.persist(savedKit, dslKit);

        verifyNoInteractions(
            deleteMaturityLevelPort,
            deleteLevelCompetencePort,
            updateMaturityLevelPort,
            updateLevelCompetencePort);
    }

    @Test
    void testPersist_ThreeLevelsInDb_TwoSameLevelsInDslAndOneIsDeleted_Delete() {
        AssessmentKit savedKit = kitWithMaturityLevels(List.of(levelOne(), levelTwo(), levelThree()));

        MaturityLevelDslModel firstDslLevel = domainToDslModel(levelOne());
        MaturityLevelDslModel secondDslLevel = domainToDslModel(levelTwo());

        List<MaturityLevelDslModel> dslLevels = List.of(firstDslLevel, secondDslLevel);

        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .maturityLevels(dslLevels)
            .build();

        persister.persist(savedKit, dslKit);

        verify(deleteMaturityLevelPort, times(1)).delete(levelThree().getId());
        verify(deleteLevelCompetencePort, times(2)).delete(any(), any());
        verifyNoInteractions(
            createMaturityLevelPort,
            createLevelCompetencePort,
            loadMaturityLevelByCodePort,
            updateMaturityLevelPort,
            updateLevelCompetencePort);
    }

    @Test
    void testPersist_DslLevelTwoHasDifferentTitle_Update() {
        AssessmentKit savedKit = kitWithMaturityLevels(List.of(levelTwo()));

        MaturityLevelDslModel dslLevel = domainToDslModel(levelTwo(), l -> l.title(levelTwo().getTitle() + "new"));
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .maturityLevels(List.of(dslLevel))
            .build();

        var updateParam = new UpdateMaturityLevelPort.Param(
            levelTwo().getId(), dslLevel.getTitle(), dslLevel.getIndex(), dslLevel.getValue());
        doNothing().when(updateMaturityLevelPort).update(updateParam);

        persister.persist(savedKit, dslKit);

        ArgumentCaptor<UpdateMaturityLevelPort.Param> updateCaptor = ArgumentCaptor.forClass(UpdateMaturityLevelPort.Param.class);
        verify(updateMaturityLevelPort).update(updateCaptor.capture());

        assertEquals(updateParam.id(), updateCaptor.getValue().id());
        assertEquals(updateParam.title(), updateCaptor.getValue().title());
        assertEquals(updateParam.index(), updateCaptor.getValue().index());
        assertEquals(updateParam.value(), updateCaptor.getValue().value());

        verifyNoInteractions(
            createMaturityLevelPort,
            createLevelCompetencePort,
            loadMaturityLevelByCodePort,
            deleteMaturityLevelPort,
            deleteLevelCompetencePort,
            updateLevelCompetencePort);
    }

    @Test
    void testPersist_DslLevelTwoHasOneMoreCompetence_Create() {
        AssessmentKit savedKit = kitWithMaturityLevels(List.of(levelOne(), levelTwo()));

        MaturityLevelDslModel dslLevelOne = domainToDslModel(levelOne());

        Map<String, Integer> dslLevelTwoCompetenceMap = competenceListToMap(levelTwo().getCompetences());
        // add competence to dslLevelTwo: affectedLevel=levelTwo, effectiveLevel=levelOne
        dslLevelTwoCompetenceMap.put(levelOne().getCode(), 90);
        MaturityLevelDslModel dslLevelTwo = domainToDslModel(levelTwo(), b -> b.competencesCodeToValueMap(dslLevelTwoCompetenceMap));

        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .maturityLevels(List.of(dslLevelOne, dslLevelTwo))
            .build();

        when(loadMaturityLevelByCodePort.loadByCode(levelOne().getCode(), savedKit.getId())).thenReturn(levelOne());
        when(createLevelCompetencePort.persist(levelTwo().getId(), levelOne().getId(), 90)).thenReturn(1L);

        persister.persist(savedKit, dslKit);

        verifyNoInteractions(
            deleteMaturityLevelPort,
            deleteLevelCompetencePort,
            createMaturityLevelPort,
            updateMaturityLevelPort,
            updateLevelCompetencePort);
    }

    @Test
    @SneakyThrows
    void testPersist_DslLevelThreeHasOneLessCompetence_Delete() {
        AssessmentKit savedKit = kitWithMaturityLevels(List.of(levelOne(), levelTwo(), levelThree()));

        Map<String, Integer> dslLevelThreeCompetenceMap = competenceListToMap(levelThree().getCompetences());
        // delete competence from dslLevelThree: affectedLevel=levelThree, effectiveLevel=levelTwo
        dslLevelThreeCompetenceMap.remove(levelTwo().getCode());

        MaturityLevelDslModel dslLevelOne = domainToDslModel(levelOne());
        MaturityLevelDslModel dslLevelTwo = domainToDslModel(levelTwo());
        MaturityLevelDslModel dslLevelThree = domainToDslModel(levelThree(), b -> b.competencesCodeToValueMap(dslLevelThreeCompetenceMap));

        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .maturityLevels(List.of(dslLevelOne, dslLevelTwo, dslLevelThree))
            .build();

        when(loadMaturityLevelByCodePort.loadByCode(levelTwo().getCode(), savedKit.getId())).thenReturn(levelTwo());
        doNothing().when(deleteLevelCompetencePort).delete(levelThree().getId(), levelTwo().getId());

        persister.persist(savedKit, dslKit);

        verifyNoInteractions(
            createMaturityLevelPort,
            createLevelCompetencePort,
            deleteMaturityLevelPort,
            updateMaturityLevelPort,
            updateLevelCompetencePort);
    }

    @Test
    void testPersist_DslLevelTwoHasOneCompetenceWithDifferenceValue_Update() {
        AssessmentKit savedKit = kitWithMaturityLevels(List.of(levelOne(), levelTwo()));

        MaturityLevelDslModel dslLevelOne = domainToDslModel(levelOne());

        Map<String, Integer> dslLevelTwoCompetenceMap = competenceListToMap(levelTwo().getCompetences());
        // change competence value from 60 to 100: affectedLevel=levelTwo, effectiveLevel=levelTwo
        dslLevelTwoCompetenceMap.put(levelTwo().getCode(), 100);
        MaturityLevelDslModel dslLevelTwo = domainToDslModel(levelTwo(), b -> b.competencesCodeToValueMap(dslLevelTwoCompetenceMap));

        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .maturityLevels(List.of(dslLevelOne, dslLevelTwo))
            .build();

        when(loadMaturityLevelByCodePort.loadByCode(levelTwo().getCode(), savedKit.getId())).thenReturn(levelTwo());
        doNothing().when(updateLevelCompetencePort).update(levelTwo().getId(), levelTwo().getId(), 100);

        persister.persist(savedKit, dslKit);

        verifyNoInteractions(
            createMaturityLevelPort,
            createLevelCompetencePort,
            deleteMaturityLevelPort,
            deleteLevelCompetencePort,
            updateMaturityLevelPort);
    }
}
