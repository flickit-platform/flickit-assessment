package org.flickit.flickitassessmentcore.application.port.in.assessment;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateAssessmentCommandTest {



    @Test
    void createAssessment_ValidCommand_CreationTimeIsAccurate() {
        LocalDateTime beforeCreateAssessment = LocalDateTime.now();
        CreateAssessmentCommand command =
            new CreateAssessmentCommand(
                "title",
                "description example",
                1L,
                1L,
                1L
            );
        LocalDateTime afterCreateAssessment = LocalDateTime.now();

        assertTrue(beforeCreateAssessment.isBefore(command.getCreationTime()));
        assertTrue(afterCreateAssessment.isAfter(command.getCreationTime()));
    }


    @Test
    void createAssessment_ValidCommand_LastModificationDateIsAccurate() {
        LocalDateTime beforeCreateAssessment = LocalDateTime.now();
        CreateAssessmentCommand command =
            new CreateAssessmentCommand(
                "title",
                "description example",
                1L,
                1L,
                1L
            );
        LocalDateTime afterCreateAssessment = LocalDateTime.now();

        assertTrue(beforeCreateAssessment.isBefore(command.getLastModificationDate()));
        assertTrue(afterCreateAssessment.isAfter(command.getLastModificationDate()));
    }

}
