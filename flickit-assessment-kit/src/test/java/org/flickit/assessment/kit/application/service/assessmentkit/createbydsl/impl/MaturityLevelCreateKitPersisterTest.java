package org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.impl;

import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.MaturityLevelDslModel;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.CreateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.CreateMaturityLevelPort;
import org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext.KEY_MATURITY_LEVELS;
import static org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother.levelOne;
import static org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother.levelTwo;
import static org.flickit.assessment.kit.test.fixture.application.dsl.MaturityLevelDslModelMother.domainToDslModel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MaturityLevelCreateKitPersisterTest {

    @InjectMocks
    private MaturityLevelCreateKitPersister persister;

    @Mock
    private CreateMaturityLevelPort createMaturityLevelPort;

    @Mock
    private CreateLevelCompetencePort createLevelCompetencePort;

    private static final Long KIT_ID = 1L;
    private static final UUID CURRENT_USER_ID = UUID.randomUUID();

    @Test
    void testOrder() {
        Assertions.assertEquals(1, persister.order());
    }

    @Test
    void testPersist_ValidInputs_SaveMaturityLevelAndItsCompetences() {
        Long kitVersionId = 1L;
        MaturityLevel levelOne = levelOne();
        MaturityLevelDslModel dslLevelOne = domainToDslModel(levelOne);
        MaturityLevel levelTwo = levelTwo();
        MaturityLevelDslModel dslLevelTwo = domainToDslModel(levelTwo);

        List<MaturityLevelDslModel> dslLevels = List.of(dslLevelOne, dslLevelTwo);

        CreateKitPersisterContext context = new CreateKitPersisterContext();
        AssessmentKitDslModel dslModel = AssessmentKitDslModel.builder()
            .maturityLevels(dslLevels)
            .build();

        MaturityLevel levelOneNoId = new MaturityLevel(null, levelOne.getCode(), levelOne.getTitle(), levelOne.getIndex(), levelOne.getDescription(), levelOne.getValue(), null);
        MaturityLevel levelTwoNoId = new MaturityLevel(null, levelTwo.getCode(), levelTwo.getTitle(), levelTwo.getIndex(), levelTwo.getDescription(), levelTwo.getValue(), null);
        when(createMaturityLevelPort.persist(levelOneNoId, KIT_ID, CURRENT_USER_ID)).thenReturn(levelOne.getId());
        when(createMaturityLevelPort.persist(levelTwoNoId, KIT_ID, CURRENT_USER_ID)).thenReturn(levelTwo.getId());

        when(createLevelCompetencePort.persist(levelTwo.getId(), levelTwo.getId(), 60, kitVersionId, CURRENT_USER_ID)).thenReturn(1L);

        persister.persist(context, dslModel, KIT_ID, CURRENT_USER_ID);

        Map<String, Long> maturityLevels = context.get(KEY_MATURITY_LEVELS);
        assertEquals(2, maturityLevels.size());
    }
}
