package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.SneakyThrows;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.MaturityLevelDslModel;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.CreateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.DeleteLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.UpdateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.CreateMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.DeleteMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.UpdateMaturityLevelPort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext.KEY_MATURITY_LEVELS;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.kitWithMaturityLevels;
import static org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother.*;
import static org.flickit.assessment.kit.test.fixture.application.dsl.MaturityLevelDslModelMother.competenceListToMap;
import static org.flickit.assessment.kit.test.fixture.application.dsl.MaturityLevelDslModelMother.domainToDslModel;
import static org.junit.jupiter.api.Assertions.*;
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

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        UpdateKitPersisterResult result = persister.persist(ctx, savedKit, dslKit, UUID.randomUUID());

        assertFalse(result.isMajorUpdate());
        Map<String, Long> codeToIdMap = ctx.get(KEY_MATURITY_LEVELS);
        assertNotNull(codeToIdMap);
        assertEquals(3, codeToIdMap.keySet().size());

        verifyNoInteractions(
            deleteMaturityLevelPort,
            deleteLevelCompetencePort,
            createMaturityLevelPort,
            createLevelCompetencePort,
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

        Long persistedLevelId = levelThree().getId();
        when(createMaturityLevelPort.persist(any(), eq(savedKit.getKitVersionId()), any(UUID.class))).thenReturn(persistedLevelId);
        UUID currentUserId = UUID.randomUUID();
        when(createLevelCompetencePort.persist(persistedLevelId, levelTwo().getId(), 75, savedKit.getKitVersionId(), currentUserId)).thenReturn(1L);
        when(createLevelCompetencePort.persist(persistedLevelId, levelThree().getId(), 60, savedKit.getKitVersionId(), currentUserId)).thenReturn(2L);

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        UpdateKitPersisterResult result = persister.persist(ctx, savedKit, dslKit, currentUserId);

        assertTrue(result.isMajorUpdate());
        Map<String, Long> codeToIdMap = ctx.get(KEY_MATURITY_LEVELS);
        assertNotNull(codeToIdMap);
        assertEquals(3, codeToIdMap.keySet().size());

        verifyNoInteractions(
            deleteMaturityLevelPort,
            deleteLevelCompetencePort,
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

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        UpdateKitPersisterResult result = persister.persist(ctx, savedKit, dslKit, UUID.randomUUID());

        assertTrue(result.isMajorUpdate());
        Map<String, Long> codeToIdMap = ctx.get(KEY_MATURITY_LEVELS);
        assertNotNull(codeToIdMap);
        assertEquals(2, codeToIdMap.keySet().size());

        verify(deleteMaturityLevelPort, times(1)).delete(levelThree().getId(), savedKit.getKitVersionId());
        verifyNoInteractions(
            createMaturityLevelPort,
            createLevelCompetencePort,
            updateLevelCompetencePort);
    }

    @Test
    void testPersist_DslLevelTwoHasDifferentTitle_Update() {
        AssessmentKit savedKit = kitWithMaturityLevels(List.of(levelTwo()));

        MaturityLevelDslModel dslLevel = domainToDslModel(levelTwo(), l -> l.title(levelTwo().getTitle() + "new"));
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .maturityLevels(List.of(dslLevel))
            .build();

        doNothing().when(updateMaturityLevelPort).updateAll(anyList(), anyLong(), any(UUID.class));

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        UpdateKitPersisterResult result = persister.persist(ctx, savedKit, dslKit, UUID.randomUUID());

        ArgumentCaptor<List<MaturityLevel>> updateCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<UUID> uuidCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(updateMaturityLevelPort).updateAll(updateCaptor.capture(), any(), uuidCaptor.capture());

        var updatedMaturityLevel = new MaturityLevel(
            levelTwo().getId(), levelTwo().getCode(), dslLevel.getTitle(), dslLevel.getIndex(), dslLevel.getDescription(), dslLevel.getValue(), levelTwo().getCompetences()
        );
        assertEquals(updatedMaturityLevel.getId(), updateCaptor.getValue().getFirst().getId());
        assertEquals(updatedMaturityLevel.getTitle(), updateCaptor.getValue().getFirst().getTitle());
        assertEquals(updatedMaturityLevel.getIndex(), updateCaptor.getValue().getFirst().getIndex());
        assertEquals(updatedMaturityLevel.getValue(), updateCaptor.getValue().getFirst().getValue());

        assertFalse(result.isMajorUpdate());
        Map<String, Long> codeToIdMap = ctx.get(KEY_MATURITY_LEVELS);
        assertNotNull(codeToIdMap);
        assertEquals(1, codeToIdMap.keySet().size());

        verifyNoInteractions(
            createMaturityLevelPort,
            createLevelCompetencePort,
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

        UUID currentUserId = UUID.randomUUID();
        when(createLevelCompetencePort.persist(levelTwo().getId(), levelOne().getId(), 90, savedKit.getKitVersionId(), currentUserId)).thenReturn(1L);

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        UpdateKitPersisterResult result = persister.persist(ctx, savedKit, dslKit, currentUserId);

        assertTrue(result.isMajorUpdate());
        Map<String, Long> codeToIdMap = ctx.get(KEY_MATURITY_LEVELS);
        assertNotNull(codeToIdMap);
        assertEquals(2, codeToIdMap.keySet().size());

        verifyNoInteractions(
            deleteMaturityLevelPort,
            deleteLevelCompetencePort,
            createMaturityLevelPort,
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

        doNothing().when(deleteLevelCompetencePort).delete(levelThree().getId(), levelTwo().getId(), savedKit.getKitVersionId());

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        UpdateKitPersisterResult result = persister.persist(ctx, savedKit, dslKit, UUID.randomUUID());

        assertTrue(result.isMajorUpdate());
        Map<String, Long> codeToIdMap = ctx.get(KEY_MATURITY_LEVELS);
        assertNotNull(codeToIdMap);
        assertEquals(3, codeToIdMap.keySet().size());

        verifyNoInteractions(
            createMaturityLevelPort,
            createLevelCompetencePort,
            deleteMaturityLevelPort,
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

        UUID uuid = UUID.randomUUID();

        doNothing().when(updateLevelCompetencePort).update(levelTwo().getId(), levelTwo().getId(), savedKit.getKitVersionId(), 100, uuid);

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        UpdateKitPersisterResult result = persister.persist(ctx, savedKit, dslKit, uuid);

        assertTrue(result.isMajorUpdate());
        Map<String, Long> codeToIdMap = ctx.get(KEY_MATURITY_LEVELS);
        assertNotNull(codeToIdMap);
        assertEquals(2, codeToIdMap.keySet().size());

        verifyNoInteractions(
            createMaturityLevelPort,
            createLevelCompetencePort,
            deleteMaturityLevelPort,
            deleteLevelCompetencePort);
    }
}
