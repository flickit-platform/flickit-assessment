package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import org.flickit.assessment.kit.application.domain.SubjectQuestionnaire;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionImpactDslModel;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.CreateSubjectQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.DeleteSubjectQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.LoadSubjectQuestionnairePort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.AttributeMother;
import org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother;
import org.flickit.assessment.kit.test.fixture.application.SubjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext.KEY_QUESTIONNAIRES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubjectQuestionnaireUpdateKitPersisterTest {

    @InjectMocks
    private SubjectQuestionnaireUpdateKitPersister persister;

    @Mock
    private LoadSubjectQuestionnairePort loadPort;

    @Mock
    private DeleteSubjectQuestionnairePort deletePort;

    @Mock
    private CreateSubjectQuestionnairePort createPort;

    @Test
    void testOrder() {
        assertEquals(6, persister.order());
    }

    @Test
    void testPersist_SameQuestionnairesForSubject_DontUpdateAnything() {
        var softwareReliability = AttributeMother.attributeWithTitle("SoftwareReliability");
        var software = SubjectMother.subjectWithTitleAndAttributes("software", List.of(softwareReliability));
        var cleanArchitecture = QuestionnaireMother.questionnaireWithTitle("CleanArchitecture");
        var savedKit = AssessmentKitMother.kitWithSubjectsAndQuestionnaires(
            List.of(software), List.of(cleanArchitecture)
        );

        var subjectQuestionnaire = new SubjectQuestionnaire(1L, software.getId(), cleanArchitecture.getId());
        when(loadPort.loadByKitVersionId(savedKit.getActiveVersionId())).thenReturn(List.of(subjectQuestionnaire));

        var questionDslModel = QuestionDslModel.builder()
            .questionnaireCode(cleanArchitecture.getCode())
            .questionImpacts(List.of(
                QuestionImpactDslModel.builder()
                    .attributeCode(softwareReliability.getCode())
                    .build()))
            .build();
        var dslKit = AssessmentKitDslModel.builder()
            .questions(List.of(questionDslModel))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        HashMap<String, Long> questionnairesCodeToIdMap = new HashMap<>();
        questionnairesCodeToIdMap.put(cleanArchitecture.getCode(), cleanArchitecture.getId());
        ctx.put(KEY_QUESTIONNAIRES, questionnairesCodeToIdMap);

        var result = persister.persist(ctx, savedKit, dslKit, UUID.randomUUID());

        assertFalse(result.isMajorUpdate());
        verifyNoInteractions(
            deletePort,
            createPort
        );
    }

    @Test
    void testPersist_AddQuestionnairesForSubject_AddAndRemoveRelation() {
        var softwareReliability = AttributeMother.attributeWithTitle("SoftwareReliability");
        var software = SubjectMother.subjectWithTitleAndAttributes("software", List.of(softwareReliability));

        var teamAgileWorkflow = AttributeMother.attributeWithTitle("TeamAgileWorkflow");
        var team = SubjectMother.subjectWithTitleAndAttributes("team", List.of(teamAgileWorkflow));

        var cleanArchitecture = QuestionnaireMother.questionnaireWithTitle("CleanArchitecture");
        var teamLearning = QuestionnaireMother.questionnaireWithTitle("TeamLearning");

        var savedKit = AssessmentKitMother.kitWithSubjectsAndQuestionnaires(
            List.of(software, team), List.of(cleanArchitecture, teamLearning)
        );

        var subjectQuestionnaire1 = new SubjectQuestionnaire(1L, software.getId(), cleanArchitecture.getId());
        var subjectQuestionnaire2 = new SubjectQuestionnaire(2L, team.getId(), cleanArchitecture.getId());
        when(loadPort.loadByKitVersionId(savedKit.getActiveVersionId())).thenReturn(List.of(subjectQuestionnaire1, subjectQuestionnaire2));

//        question impacts on softwareReliability for software subject in cleanArchitecture questionnaire
        var questionDslModel = QuestionDslModel.builder()
            .questionnaireCode(cleanArchitecture.getCode())
            .questionImpacts(List.of(
                QuestionImpactDslModel.builder()
                    .attributeCode(softwareReliability.getCode())
                    .build()
            ))
            .build();
//        question impacts on teamAgileWorkflow for team subject in teamLearning questionnaire
        var questionDslModel2 = QuestionDslModel.builder()
            .questionnaireCode(teamLearning.getCode())
            .questionImpacts(List.of(
                QuestionImpactDslModel.builder()
                    .attributeCode(teamAgileWorkflow.getCode())
                    .build()
            ))
            .build();
        var dslKit = AssessmentKitDslModel.builder()
            .questions(List.of(questionDslModel, questionDslModel2))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        HashMap<String, Long> questionnairesCodeToIdMap = new HashMap<>();
        questionnairesCodeToIdMap.put(cleanArchitecture.getCode(), cleanArchitecture.getId());
        questionnairesCodeToIdMap.put(teamLearning.getCode(), teamLearning.getId());
        ctx.put(KEY_QUESTIONNAIRES, questionnairesCodeToIdMap);

        var result = persister.persist(ctx, savedKit, dslKit, UUID.randomUUID());
        assertFalse(result.isMajorUpdate());

        verify(deletePort, times(1)).delete(subjectQuestionnaire2.getId());

        var subjectIdArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        var questionnaireIdArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        var kitVersionIdArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(createPort, times(1)).persist(subjectIdArgumentCaptor.capture(), questionnaireIdArgumentCaptor.capture(), kitVersionIdArgumentCaptor.capture());
        assertEquals(team.getId(), subjectIdArgumentCaptor.getValue());
        assertEquals(teamLearning.getId(), questionnaireIdArgumentCaptor.getValue());
        assertEquals(savedKit.getActiveVersionId(), kitVersionIdArgumentCaptor.getValue());
    }
}
