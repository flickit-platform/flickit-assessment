package org.flickit.assessment.kit.adapter.out.persistence.kitdsl.convertor;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class QuestionnairesConverterTest {

    @Test
    void testQuestionnairesConvertor() throws IOException {
        var questionnairesSheet = createWorkbook().getSheet("Questionnaires");
        var questionnaires = QuestionnairesConverter.convert(questionnairesSheet);

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

    private Workbook createWorkbook() throws IOException {

        File file = new File("src/test/resources/correct-excel-kit.xlsx");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
            file.getName(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", IOUtils.toByteArray(input));

        return new XSSFWorkbook(multipartFile.getInputStream());
    }
}
