package org.flickit.assessment.kit.application.service;

import lombok.SneakyThrows;
import org.flickit.assessment.kit.application.domain.dsl.*;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DslTranslatorTest {

    @Test
    @SneakyThrows
    void testJsonParser() {
        String file = "src/test/resources/dsl.json";
        String json = new String(Files.readAllBytes(Path.of(file)));

        AssessmentKitDslModel kit = DslTranslator.parseJson(json);
        assertNotNull(kit);

        List<SubjectDslModel> subjects = kit.getSubjects();
        List<AttributeDslModel> attributes = kit.getAttributes();
        List<QuestionnaireDslModel> questionnaires = kit.getQuestionnaires();
        List<QuestionDslModel> questions = kit.getQuestions();
        List<MaturityLevelDslModel> maturityLevels = kit.getMaturityLevels();

        assertNotNull(kit.getSubjects());
        assertEquals(2, subjects.size());
        assertEquals(2, subjects.stream().map(BaseDslModel::getCode).distinct().toList().size());

        assertNotNull(kit.getAttributes());
        assertEquals(2, attributes.size());
        assertEquals(2, attributes.stream().map(BaseDslModel::getCode).distinct().toList().size());

        assertNotNull(kit.getQuestionnaires());
        assertEquals(2, questionnaires.size());
        assertEquals(2, questionnaires.stream().map(BaseDslModel::getCode).distinct().toList().size());

        assertNotNull(kit.getQuestions());
        assertEquals(2, questions.size());
        assertEquals(2, questions.stream().map(BaseDslModel::getCode).distinct().toList().size());
        questions.forEach(q -> {
            assertNotNull(q.getQuestionImpacts());
            assertNotNull(q.getAnswerOptions());
        });

        assertNotNull(kit.getMaturityLevels());
        assertEquals(5, maturityLevels.size());
        assertEquals(5, maturityLevels.stream().map(BaseDslModel::getCode).distinct().toList().size());

        assertFalse(kit.isHasError());
    }

}
