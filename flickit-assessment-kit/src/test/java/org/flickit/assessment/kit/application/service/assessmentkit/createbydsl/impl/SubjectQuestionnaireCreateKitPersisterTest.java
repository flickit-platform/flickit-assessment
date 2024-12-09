package org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.impl;

import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionImpactDslModel;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.CreateSubjectQuestionnairePort;
import org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext;
import org.flickit.assessment.kit.test.fixture.application.AttributeMother;
import org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother;
import org.flickit.assessment.kit.test.fixture.application.SubjectMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.AttributeDslModelMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext.KEY_SUBJECTS;
import static org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext.KEY_QUESTIONNAIRES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class SubjectQuestionnaireCreateKitPersisterTest {

    private static final Long KIT_ID = 1L;
    private static final UUID CURRENT_USER_ID = UUID.randomUUID();
    @InjectMocks
    private SubjectQuestionnaireCreateKitPersister persister;
    @Mock
    private CreateSubjectQuestionnairePort createSubjectQuestionnairePort;

    @Test
    void testOrder() {
        assertEquals(7, persister.order());
    }

    @Test
    void testPersist_ValidInputs_SaveSubjectQuestionnaire() {
        var attribute = AttributeMother.attributeWithTitle("SoftwareReliability");
        var subject = SubjectMother.subjectWithTitleAndAttributes("software", List.of(attribute));
        var questionnaire = QuestionnaireMother.questionnaireWithTitle("CleanArchitecture");

        var attributeDslModel = AttributeDslModelMother.domainToDslModel(attribute, a -> a.subjectCode(subject.getCode()));
        var questionDslModel = QuestionDslModel.builder()
            .questionnaireCode(questionnaire.getCode())
            .questionImpacts(List.of(
                QuestionImpactDslModel.builder()
                    .attributeCode(attribute.getCode())
                    .build()))
            .build();
        var dslModel = AssessmentKitDslModel.builder()
            .questions(List.of(questionDslModel))
            .attributes(List.of(attributeDslModel))
            .build();

        CreateKitPersisterContext context = new CreateKitPersisterContext();
        context.put(KEY_QUESTIONNAIRES, Map.of(questionnaire.getCode(), questionnaire.getId()));
        context.put(KEY_SUBJECTS, Map.of(subject.getCode(), subject.getId()));

        doNothing().when(createSubjectQuestionnairePort).persistAll(Map.of(questionnaire.getId(), Set.of(subject.getId())), KIT_ID);

        persister.persist(context, dslModel, KIT_ID, CURRENT_USER_ID);

        // TODO: assert?
    }
}
