package org.flickit.assessment.kit.adapter.out.persistence.kitdsl.converter;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AttributeDslModel;
import org.flickit.assessment.kit.application.domain.dsl.MaturityLevelDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionDslModel;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class QuestionsConvertorTest {
    AttributeDslModel attributeDsl1 = AttributeDslModel.builder().subjectCode("Team").weight(1).code("AttributeOne").index(1).title("Attribute One").description("Attribute One Description").build();
    AttributeDslModel attributeDsl2 = AttributeDslModel.builder().subjectCode("Team").weight(3).code("AttributeTwo").index(2).title("Attribute Two").description("Attribute Two Description").build();
    AttributeDslModel attributeDsl3 = AttributeDslModel.builder().subjectCode("Software").weight(2).code("AttributeThree").index(3).title("Attribute Three").description("Attribute Three Description").build();
    List<AttributeDslModel> attributes = List.of(attributeDsl1, attributeDsl2, attributeDsl3);

    MaturityLevelDslModel level1 = MaturityLevelDslModel.builder().value(1).code("Unprepared").index(1).title("Unprepared Title").description("Unprepared Description").build();
    MaturityLevelDslModel level2 = MaturityLevelDslModel.builder().value(2).code("Prepared").index(2).title("Prepared Title").description("Prepared Description").build();
    MaturityLevelDslModel level3 = MaturityLevelDslModel.builder().value(3).code("WellEquipped").index(3).title("Well Equipped Title").description("Well Equipped Description").build();
    MaturityLevelDslModel level4 = MaturityLevelDslModel.builder().value(4).code("SateOfTheArt").index(4).title("State of The Art Title").description("State of The Art Description").build();
    Map<String, MaturityLevelDslModel> maturityLevelCodeToMaturityLevelDslMap = new HashMap<>();

    AnswerOptionDslModel option1 = AnswerOptionDslModel.builder().index(1).caption("Never").value(0.0).build();
    AnswerOptionDslModel option2 = AnswerOptionDslModel.builder().index(2).caption("Often").value(0.8).build();
    AnswerOptionDslModel option3 = AnswerOptionDslModel.builder().index(3).caption("Always").value(1.0).build();
    AnswerOptionDslModel option4 = AnswerOptionDslModel.builder().index(1).caption("No").value(0.0).build();
    AnswerOptionDslModel option5 = AnswerOptionDslModel.builder().index(2).caption("Yes").value(1.0).build();
    Map<String, List<AnswerOptionDslModel>> answerRangeCodeToAnswerOptionsMap = new HashMap<>();

    @Test
    void testQuestionsConvertor() throws IOException {
        maturityLevelCodeToMaturityLevelDslMap.put("Unprepared", level1);
        maturityLevelCodeToMaturityLevelDslMap.put("Prepared", level2);
        maturityLevelCodeToMaturityLevelDslMap.put("WellEquipped", level3);
        maturityLevelCodeToMaturityLevelDslMap.put("SateOfTheArt", level4);

        answerRangeCodeToAnswerOptionsMap.put("UsageRange", List.of(option1, option2, option3));
        answerRangeCodeToAnswerOptionsMap.put("YesNo", List.of(option4, option5));

        var questionsSheet = createWorkbook().getSheet("Questions");
        var questions = QuestionsConvertor.convert(questionsSheet, answerRangeCodeToAnswerOptionsMap, maturityLevelCodeToMaturityLevelDslMap, attributes);

        assertEquals(5, questions.size());
        //Assert Question 1
        QuestionDslModel question = questions.getFirst();
        assertEquals(1, question.getIndex());
        assertEquals("Question Development 1", question.getTitle());
        assertEquals("Development", question.getQuestionnaireCode());
        assertEquals("Q1", question.getCode());
        assertEquals("UsageRange", question.getAnswerRangeCode());
        assertEquals("Question Development 1 Description", question.getDescription());
        assertEquals(1, question.getQuestionImpacts().size());
        //Question 1 : QuestionImpacts
        assertEquals("AttributeTwo", question.getQuestionImpacts().getFirst().getAttributeCode());
        assertEquals(1, question.getQuestionImpacts().getFirst().getWeight());
        assertEquals("Prepared Title", question.getQuestionImpacts().getFirst().getMaturityLevel().getTitle());
        assertEquals("Prepared", question.getQuestionImpacts().getFirst().getMaturityLevel().getCode());
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
        assertEquals(1, question.getQuestionImpacts().size());
        //Question 2 : QuestionImpacts
        assertEquals("AttributeTwo", question.getQuestionImpacts().getFirst().getAttributeCode());
        assertEquals(1, question.getQuestionImpacts().getFirst().getWeight());
        assertEquals("Prepared Title", question.getQuestionImpacts().getFirst().getMaturityLevel().getTitle());
        assertEquals("Prepared", question.getQuestionImpacts().getFirst().getMaturityLevel().getCode());
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
        assertEquals(1, question.getQuestionImpacts().size());
        //Question 3 : QuestionImpacts
        assertEquals("AttributeTwo", question.getQuestionImpacts().getFirst().getAttributeCode());
        assertEquals(2, question.getQuestionImpacts().getFirst().getWeight());
        assertEquals("Well Equipped Title", question.getQuestionImpacts().getFirst().getMaturityLevel().getTitle());
        assertEquals("WellEquipped", question.getQuestionImpacts().getFirst().getMaturityLevel().getCode());
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
        assertEquals(2, question.getQuestionImpacts().size());
        //Question 4 : QuestionImpacts
        assertEquals("AttributeOne", question.getQuestionImpacts().getFirst().getAttributeCode());
        assertEquals(1, question.getQuestionImpacts().getFirst().getWeight());
        assertEquals("Well Equipped Title", question.getQuestionImpacts().getFirst().getMaturityLevel().getTitle());
        assertEquals("WellEquipped", question.getQuestionImpacts().getFirst().getMaturityLevel().getCode());

        assertEquals("AttributeThree", question.getQuestionImpacts().getLast().getAttributeCode());
        assertEquals(1, question.getQuestionImpacts().getLast().getWeight());
        assertEquals("Well Equipped Title", question.getQuestionImpacts().getLast().getMaturityLevel().getTitle());
        assertEquals("WellEquipped", question.getQuestionImpacts().getLast().getMaturityLevel().getCode());
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
        assertEquals(3, question.getQuestionImpacts().size());
        //Question 4 : QuestionImpacts
        assertEquals("AttributeOne", question.getQuestionImpacts().getFirst().getAttributeCode());
        assertEquals(1, question.getQuestionImpacts().getFirst().getWeight());
        assertEquals("Well Equipped Title", question.getQuestionImpacts().getFirst().getMaturityLevel().getTitle());
        assertEquals("WellEquipped", question.getQuestionImpacts().getFirst().getMaturityLevel().getCode());

        assertEquals("AttributeTwo", question.getQuestionImpacts().get(1).getAttributeCode());
        assertEquals(1, question.getQuestionImpacts().get(1).getWeight());
        assertEquals("Well Equipped Title", question.getQuestionImpacts().get(1).getMaturityLevel().getTitle());
        assertEquals("WellEquipped", question.getQuestionImpacts().get(1).getMaturityLevel().getCode());

        assertEquals("AttributeThree", question.getQuestionImpacts().getLast().getAttributeCode());
        assertEquals(1, question.getQuestionImpacts().getFirst().getWeight());
        assertEquals("Well Equipped Title", question.getQuestionImpacts().getLast().getMaturityLevel().getTitle());
        assertEquals("WellEquipped", question.getQuestionImpacts().getLast().getMaturityLevel().getCode());
    }

    private Workbook createWorkbook() throws IOException {
        File file = new File("src/test/resources/correct-excel-kit.xlsx");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
            file.getName(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", IOUtils.toByteArray(input));

        return new XSSFWorkbook(multipartFile.getInputStream());
    }
}
