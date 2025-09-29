package org.flickit.assessment.kit.adapter.out.persistence.kitdsl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flickit.assessment.kit.application.domain.dsl.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class KitDslPersistenceJpaAdapterTest {

    @InjectMocks
    KitDslPersistenceJpaAdapter adapter;

    @Test
    void testExcelToDslModelConverterTest_testConvertSubjects() throws IOException {
        final Workbook workbook = createWorkbook();

        MultipartFile multipartFile = toMultipartFile(workbook);

        var dslModel = adapter.convert(multipartFile);

        assertQualityAttributes(dslModel);
        assertQuestionnaires(dslModel.getQuestionnaires());
        assertMaturityLevels(dslModel.getMaturityLevels());
        assertQuestions(dslModel.getQuestions());
    }

    private Workbook createWorkbook() throws IOException {

        File file = new File("src/test/resources/correct-excel-kit.xlsx");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
            file.getName(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", IOUtils.toByteArray(input));

        return new XSSFWorkbook(multipartFile.getInputStream());
    }

    public MultipartFile toMultipartFile(Workbook workbook) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            workbook.close();
            return new MockMultipartFile(
                "file",
                "fileName",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                baos.toByteArray()
            );
        }
    }

    private static void assertQualityAttributes(AssessmentKitDslModel dslModel) {
        //Assert subject 1
        assertEquals(2, dslModel.getSubjects().size());
        SubjectDslModel subject1 = dslModel.getSubjects().getFirst();
        assertEquals("Team", subject1.getCode());
        assertEquals("Team Title", subject1.getTitle());
        assertEquals(2, subject1.getWeight());
        assertEquals("Subject Description 1", subject1.getDescription());
        assertEquals(1, subject1.getIndex());
        //Assert Attribute 1
        AttributeDslModel attr1 = dslModel.getAttributes().getFirst();
        assertEquals("Team", attr1.getSubjectCode());
        assertEquals("AttributeOne", attr1.getCode());
        assertEquals("Attribute One", attr1.getTitle());
        assertEquals(1, attr1.getWeight());
        assertEquals("Attribute One Description", attr1.getDescription());
        assertEquals(1, attr1.getIndex());
        //Assert Attribute 2
        AttributeDslModel attr2 = dslModel.getAttributes().get(1);
        assertEquals("Team", attr2.getSubjectCode());
        assertEquals("AttributeTwo", attr2.getCode());
        assertEquals("Attribute Two", attr2.getTitle());
        assertEquals(3, attr2.getWeight());
        assertEquals("Attribute Two Description", attr2.getDescription());
        assertEquals(2, attr2.getIndex());
        //Assert subject 2
        SubjectDslModel subject2 = dslModel.getSubjects().getLast();
        assertEquals("Software", subject2.getCode());
        assertEquals("Software Title", subject2.getTitle());
        assertEquals(1, subject2.getWeight());
        assertEquals("Subject Description 2", subject2.getDescription());
        assertEquals(2, subject2.getIndex());
        //Assert Attribute 3
        AttributeDslModel attr3 = dslModel.getAttributes().getLast();
        assertEquals("Software", attr3.getSubjectCode());
        assertEquals("AttributeThree", attr3.getCode());
        assertEquals("Attribute Three", attr3.getTitle());
        assertEquals(2, attr3.getWeight());
        assertEquals("Attribute Three Description", attr3.getDescription());
        assertEquals(3, attr3.getIndex());
    }

    private static void assertQuestionnaires(List<QuestionnaireDslModel> questionnaires) {
        //Assert questionnaire
        assertEquals(3, questionnaires.size());
        //Assert Questionnaire 1
        QuestionnaireDslModel questionnaire = questionnaires.getFirst();
        assertEquals("DevelopmentCode", questionnaire.getCode());
        assertEquals("Development", questionnaire.getTitle());
        assertEquals("Development Description", questionnaire.getDescription());
        //Assert Questionnaire 2
        questionnaire = questionnaires.get(1);
        assertEquals("DevOpsCode", questionnaire.getCode());
        assertEquals("DevOps", questionnaire.getTitle());
        assertEquals("DevOps Description", questionnaire.getDescription());
        //Assert Questionnaire 3
        questionnaire = questionnaires.getLast();
        assertEquals("TeamCollaborationCode", questionnaire.getCode());
        assertEquals("Team Collaboration", questionnaire.getTitle());
        assertEquals("Team Collaboration Description", questionnaire.getDescription());
    }

    private static void assertQuestions(List<QuestionDslModel> questions) {
        //Asser Questions
        assertEquals(5, questions.size());
        //Assert Question 1
        QuestionDslModel question = questions.getFirst();
        assertEquals(1, question.getIndex());
        assertEquals("Question Development 1", question.getTitle());
        assertEquals("Development", question.getQuestionnaireCode());
        assertEquals("Q1", question.getCode());
        assertEquals("UsageRange", question.getAnswerRangeCode());
        assertEquals("Question Development 1 Description", question.getDescription());
        assertFalse(question.isMayNotBeApplicable());
        assertTrue(question.isAdvisable());
        //Assert Question 2
        question = questions.get(1);
        assertEquals(2, question.getIndex());
        assertEquals("Question Development 2", question.getTitle());
        assertEquals("Development", question.getQuestionnaireCode());
        assertEquals("Q2", question.getCode());
        assertEquals("UsageRange", question.getAnswerRangeCode());
        assertEquals("Question Development 2 Description", question.getDescription());
        assertFalse(question.isMayNotBeApplicable());
        assertTrue(question.isAdvisable());
        //Assert Question 3
        question = questions.get(2);
        assertEquals(3, question.getIndex());
        assertEquals("Question DevOps 1", question.getTitle());
        assertEquals("DevOps", question.getQuestionnaireCode());
        assertEquals("Q13", question.getCode());
        assertEquals("UsageRange", question.getAnswerRangeCode());
        assertEquals("Question DevOps 1 Description", question.getDescription());
        assertTrue(question.isMayNotBeApplicable());
        assertTrue(question.isAdvisable());
        //Assert Question 4
        question = questions.get(3);
        assertEquals(4, question.getIndex());
        assertEquals("Question TeamCollaboration 1", question.getTitle());
        assertEquals("TeamCollaboration", question.getQuestionnaireCode());
        assertEquals("Q20", question.getCode());
        assertEquals("UsageRange", question.getAnswerRangeCode());
        assertEquals("Question TeamCollaboration 1 Description", question.getDescription());
        assertFalse(question.isMayNotBeApplicable());
        assertFalse(question.isAdvisable());
        //Assert Question 5
        question = questions.get(4);
        assertEquals(5, question.getIndex());
        assertEquals("Question TeamCollaboration 2", question.getTitle());
        assertEquals("TeamCollaboration", question.getQuestionnaireCode());
        assertEquals("Q22", question.getCode());
        assertEquals("UsageRange", question.getAnswerRangeCode());
        assertEquals("Question TeamCollaboration 2 Description", question.getDescription());
        assertTrue(question.isMayNotBeApplicable());
        assertFalse(question.isAdvisable());
    }

    private static void assertMaturityLevels(List<MaturityLevelDslModel> maturityLevels) {
        assertEquals(4, maturityLevels.size());

        // 1: Unprepared
        MaturityLevelDslModel maturityLevel = maturityLevels.getFirst();
        assertEquals(1, maturityLevel.getIndex());
        assertEquals(1, maturityLevel.getValue());
        assertEquals("Unprepared", maturityLevel.getCode());
        assertEquals("Unprepared Title", maturityLevel.getTitle());
        assertEquals("Unprepared Description", maturityLevel.getDescription());
        assertTrue(maturityLevel.getCompetencesCodeToValueMap().isEmpty());

        // 2: Prepared
        maturityLevel = maturityLevels.get(1);
        assertEquals(2, maturityLevel.getIndex());
        assertEquals(2, maturityLevel.getValue());
        assertEquals("Prepared", maturityLevel.getCode());
        assertEquals("Prepared Title", maturityLevel.getTitle());
        assertEquals("Prepared Description", maturityLevel.getDescription());
        assertEquals(1, maturityLevel.getCompetencesCodeToValueMap().size());
        assertEquals(70, maturityLevel.getCompetencesCodeToValueMap().get("Prepared"));

        // 3: WellEquipped
        maturityLevel = maturityLevels.get(2);
        assertEquals(3, maturityLevel.getIndex());
        assertEquals(3, maturityLevel.getValue());
        assertEquals("WellEquipped", maturityLevel.getCode());
        assertEquals("Well Equipped Title", maturityLevel.getTitle());
        assertEquals("Well Equipped Description", maturityLevel.getDescription());
        assertEquals(2, maturityLevel.getCompetencesCodeToValueMap().size());
        assertEquals(80, maturityLevel.getCompetencesCodeToValueMap().get("Prepared"));
        assertEquals(70, maturityLevel.getCompetencesCodeToValueMap().get("WellEquipped"));

        // 4: WellEquipped
        maturityLevel = maturityLevels.getLast();
        assertEquals(4, maturityLevel.getIndex());
        assertEquals(4, maturityLevel.getValue());
        assertEquals("StateOfTheArt", maturityLevel.getCode());
        assertEquals("State of the Art Title", maturityLevel.getTitle());
        assertEquals("State of the Art Description", maturityLevel.getDescription());
        assertEquals(3, maturityLevel.getCompetencesCodeToValueMap().size());
        assertEquals(80, maturityLevel.getCompetencesCodeToValueMap().get("Prepared"));
        assertEquals(80, maturityLevel.getCompetencesCodeToValueMap().get("WellEquipped"));
        assertEquals(40, maturityLevel.getCompetencesCodeToValueMap().get("StateOfTheArt"));
    }
}
