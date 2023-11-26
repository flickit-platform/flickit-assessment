package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.SneakyThrows;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.out.questionnaire.CreateQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.UpdateQuestionnaireByKitPort;
import org.flickit.assessment.kit.application.service.DslTranslator;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionnaireUpdateKitPersisterTest {

    public static final String FILE = "src/test/resources/dsl.json";

    @InjectMocks
    private QuestionnaireUpdateKitPersister persister;

    @Mock
    private CreateQuestionnairePort createQuestionnairePort;

    @Mock
    private UpdateQuestionnaireByKitPort updateQuestionnaireByKitPort;

    @Test
    @SneakyThrows
    void testQuestionnaireUpdateKitPersister_SameInputsAsDatabaseData_NoChange() {
        Long kitId = 1L;
        var savedQuestionnaire1 = QuestionnaireMother.questionnaire("CleanArchitecture", "Clean Architecture", 1);
        var savedQuestionnaire2 = QuestionnaireMother.questionnaire("CodeQuality", "Code Quality", 2);
        AssessmentKit savedKit = AssessmentKitMother.kitWithQuestionnaire(List.of(savedQuestionnaire1, savedQuestionnaire2), kitId);

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        AssessmentKitDslModel dslKit = DslTranslator.parseJson(dslContent);
        persister.persist(savedKit, dslKit);

        verifyNoInteractions(createQuestionnairePort, updateQuestionnaireByKitPort);
    }

    @Test
    @SneakyThrows
    void testQuestionnaireUpdateKitPersister_QuestionnaireAdded_AddToDatabase() {
        Long kitId = 1L;
        var savedQuestionnaire1 = QuestionnaireMother.questionnaire("CleanArchitecture", "Clean Architecture", 1);
        AssessmentKit savedKit = AssessmentKitMother.kitWithQuestionnaire(List.of(savedQuestionnaire1), kitId);

        when(createQuestionnairePort.persist(any(Questionnaire.class), eq(kitId))).thenReturn(1L);

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        AssessmentKitDslModel dslKit = DslTranslator.parseJson(dslContent);
        persister.persist(savedKit, dslKit);

        verify(createQuestionnairePort, times(1)).persist(any(Questionnaire.class), eq(kitId));
        verifyNoInteractions(updateQuestionnaireByKitPort);
    }

    @Test
    @SneakyThrows
    void testQuestionnaireUpdateKitPersister_QuestionnaireUpdated_UpdateInDatabase() {
        Long kitId = 1L;
        var savedQuestionnaire1 = QuestionnaireMother.questionnaire("CleanArchitecture", "Clean Architecture", 1);
        var savedQuestionnaire2 = QuestionnaireMother.questionnaire("CodeQuality", "Old Code Quality", 2);
        AssessmentKit savedKit = AssessmentKitMother.kitWithQuestionnaire(List.of(savedQuestionnaire1, savedQuestionnaire2), kitId);

        doNothing().when(updateQuestionnaireByKitPort).updateByKitId(any(UpdateQuestionnaireByKitPort.Param.class));

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        AssessmentKitDslModel dslKit = DslTranslator.parseJson(dslContent);
        persister.persist(savedKit, dslKit);

        verify(updateQuestionnaireByKitPort, times(1)).updateByKitId(any(UpdateQuestionnaireByKitPort.Param.class));
        verifyNoInteractions(createQuestionnairePort);
    }
}
