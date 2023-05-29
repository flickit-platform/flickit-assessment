package org.flickit.flickitassessmentcore.application.port.in.AssessmentProject;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateAssessmentProjectCommandTest {

    @Test
    void generateSlugCodeByTitle_NoWhitespace_ReturnsLowerCaseCode() {
        String title = "ExampleTitle";
        CreateAssessmentProjectCommand command = createCommandWithTitle(title);

        String code = command.generateSlugCodeByTitle();

        assertEquals("exampletitle", code);
    }

    @Test
    void generateSlugCodeByTitle_WithWhitespace_ReturnsLowerCaseCodeWithHyphens() {
        String title = "Example Title with Whitespace";
        CreateAssessmentProjectCommand command = createCommandWithTitle(title);

        String code = command.generateSlugCodeByTitle();

        assertEquals("example-title-with-whitespace", code);
    }

    @Test
    void generateSlugCodeByTitle_WithLeadingAndTrailingWhitespace_ReturnsLowerCaseCodeWithHyphens() {
        String title = "  Example Title with   Leading and Trailing   Whitespace  ";
        CreateAssessmentProjectCommand command = createCommandWithTitle(title);

        String code = command.generateSlugCodeByTitle();

        assertEquals("example-title-with-leading-and-trailing-whitespace", code);
    }


    @Test
    void createAssessmentProject_ValidCommand_CreationTimeIsAccurate() {
        LocalDateTime beforeCreateAssessment = LocalDateTime.now();
        CreateAssessmentProjectCommand command =
            new CreateAssessmentProjectCommand(
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
    void createAssessmentProject_ValidCommand_LastModificationDateIsAccurate() {
        LocalDateTime beforeCreateAssessment = LocalDateTime.now();
        CreateAssessmentProjectCommand command =
            new CreateAssessmentProjectCommand(
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

    private static CreateAssessmentProjectCommand createCommandWithTitle(String title) {
        return new CreateAssessmentProjectCommand(
            title,
            "description example",
            1L,
            1L,
            1L
        );
    }

}
