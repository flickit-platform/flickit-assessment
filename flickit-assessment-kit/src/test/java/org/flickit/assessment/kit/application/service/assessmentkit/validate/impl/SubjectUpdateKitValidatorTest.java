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

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.COLLECTION;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SubjectUpdateKitValidatorTest {

    public static final String FILE = "src/test/resources/dsl.json";

    private SubjectUpdateKitValidator validator;
    private AssessmentKitDslModel dslKit;

    @BeforeEach
    @SneakyThrows
    void init() {
        if (validator == null) {
            validator = new SubjectUpdateKitValidator();

            String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
            dslKit = DslTranslator.parseJson(dslContent);
        }
    }

    @Test
    void testValidate_SameSizeWithSavedAndNotChangeCodes_ValidChange() {
        Long kitId = 1L;
        AssessmentKit savedKit = AssessmentKitMother.kitWithTwoSubject(kitId);

        Notification notification = validator.validate(savedKit, dslKit);

        assertFalse(notification.hasErrors());
    }

    @Test
    void testValidate_AddNewSubject_InvalidChange() {
        Long kitId = 1L;
        AssessmentKit savedKit = AssessmentKitMother.kitWithOneSubject(kitId);

        Notification notification = validator.validate(savedKit, dslKit);

        assertThat(notification)
            .returns(true, Notification::hasErrors)
            .extracting(Notification::getErrors, as(COLLECTION))
            .singleElement()
            .isInstanceOfSatisfying(InvalidAdditionError.class, x -> {
                assertThat(x.fieldName()).isEqualTo("subject");
                assertThat(x.addedItems()).contains("Team");
            });
    }

    @Test
    void testValidate_DeleteSubject_InvalidChange() {
        Long kitId = 1L;
        AssessmentKit savedKit = AssessmentKitMother.kitWithThreeSubject(kitId);

        Notification notification = validator.validate(savedKit, dslKit);

        assertThat(notification)
            .returns(true, Notification::hasErrors)
            .extracting(Notification::getErrors, as(COLLECTION))
            .singleElement()
            .isInstanceOfSatisfying(InvalidDeletionError.class, x -> {
                assertThat(x.fieldName()).isEqualTo("subject");
                assertThat(x.deletedItems()).contains("Security");
            });
    }
}
