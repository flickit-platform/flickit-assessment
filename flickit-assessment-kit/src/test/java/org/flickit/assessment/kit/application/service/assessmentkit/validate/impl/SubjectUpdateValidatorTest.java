package org.flickit.assessment.kit.application.service.assessmentkit.validate.impl;

import lombok.SneakyThrows;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.service.DslTranslator;
import org.flickit.assessment.kit.common.Notification;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class SubjectUpdateValidatorTest {

    public static final String FILE = "src/test/resources/dsl.json";

    private SubjectUpdateValidator validator;
    private AssessmentKitDslModel dslKit;

    @BeforeEach
    @SneakyThrows
    void init() {
        if (validator == null) {
            validator = new SubjectUpdateValidator();

            String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
            dslKit = DslTranslator.parseJson(dslContent);
        }
    }

    @Test
    void testSubjectUpdateValidator_SameSizeWithSavedAndNotChangeCodes_ValidChange() {
        Long kitId = 1L;
        AssessmentKit savedKit = AssessmentKitMother.kitWithTwoSubject(kitId);

        Notification notification = validator.validate(savedKit, dslKit);

        assertFalse(notification.hasErrors());
        assertTrue(notification.getErrors().isEmpty());
    }

    @Test
    void testSubjectUpdateValidator_AddNewSubject_NotValidChange() {
        Long kitId = 1L;
        AssessmentKit savedKit = AssessmentKitMother.kitWithOneSubject(kitId);

        Notification notification = validator.validate(savedKit, dslKit);

        assertTrue(notification.hasErrors());
        assertEquals(1, notification.getErrors().size());
        assertTrue(notification.getErrors().contains(UPDATE_SUBJECT_BY_DSL_SUBJECT_NOT_ADD));
    }

    @Test
    void testSubjectUpdateValidator_RemoveSubject_NotValidChange() {
        Long kitId = 1L;
        AssessmentKit savedKit = AssessmentKitMother.kitWithThreeSubject(kitId);

        Notification notification = validator.validate(savedKit, dslKit);

        assertTrue(notification.hasErrors());
        assertEquals(1, notification.getErrors().size());
        assertTrue(notification.getErrors().contains(UPDATE_SUBJECT_BY_DSL_SUBJECT_NOT_REMOVE));
    }

    @Test
    void testSubjectUpdateValidator_ChangeSubjectCode_NotValidChange() {
        Long kitId = 1L;
        AssessmentKit savedKit = AssessmentKitMother.kitWithTwoSubjectDiffCode(kitId);

        Notification notification = validator.validate(savedKit, dslKit);

        assertTrue(notification.hasErrors());
        assertEquals(1, notification.getErrors().size());
        assertTrue(notification.getErrors().contains(UPDATE_SUBJECT_BY_DSL_SUBJECT_CODE_NOT_CHANGE));
    }
}
