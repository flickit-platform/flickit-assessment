package org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.validate.impl;

import org.flickit.assessment.common.exception.api.Notification;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;
import org.flickit.assessment.kit.test.fixture.application.SubjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.COLLECTION;
import static org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.validate.impl.DslFieldNames.SUBJECT;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.kitWithSubjects;
import static org.flickit.assessment.kit.test.fixture.application.SubjectMother.subjectWithTitle;
import static org.flickit.assessment.kit.test.fixture.application.dsl.SubjectDslModelMother.domainToDslModel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class SubjectUpdateKitValidatorTest {

    @InjectMocks
    private SubjectUpdateKitValidator validator;

    @Test
    void testValidate_SameSubjectCodesInDbAndDsl_Valid() {
        Subject subjectOne = SubjectMother.subjectWithTitle("Software");
        Subject subjectTwo = subjectWithTitle("Team");
        AssessmentKit savedKit = kitWithSubjects(List.of(subjectOne, subjectTwo));

        SubjectDslModel dslSubjectOne = domainToDslModel(subjectOne, b -> b.title("new title"));
        SubjectDslModel dslSubjectTwo = domainToDslModel(subjectTwo, b -> b.description("new description"));
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .subjects(List.of(dslSubjectOne, dslSubjectTwo))
            .build();

        Notification notification = validator.validate(savedKit, dslKit);

        assertFalse(notification.hasErrors());
    }

    @Test
    void testValidate_dslHasTwoNewSubjects_valid() {
        Subject subjectOne = SubjectMother.subjectWithTitle("Software");
        Subject subjectTwo = subjectWithTitle("Team");
        AssessmentKit savedKit = kitWithSubjects(List.of(subjectOne, subjectTwo));

        Subject subjectThree = subjectWithTitle("Security");
        Subject subjectFour = subjectWithTitle("Company");
        SubjectDslModel dslSubjectOne = domainToDslModel(subjectOne);
        SubjectDslModel dslSubjectTwo = domainToDslModel(subjectTwo);
        SubjectDslModel dslSubjectThree = domainToDslModel(subjectThree);
        SubjectDslModel dslSubjectFour = domainToDslModel(subjectFour);

        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .subjects(List.of(dslSubjectOne, dslSubjectTwo, dslSubjectThree, dslSubjectFour))
            .build();

        Notification notification = validator.validate(savedKit, dslKit);

        assertThat(notification)
            .returns(false, Notification::hasErrors);
        assertEquals(0, notification.getErrors().size());
    }

    @Test
    void testValidate_dslHasTwoSubjectsLessThanDb_Invalid() {
        Subject subjectOne = SubjectMother.subjectWithTitle("Software");
        Subject subjectTwo = subjectWithTitle("Team");
        Subject subjectThree = subjectWithTitle("Security");
        Subject subjectFour = subjectWithTitle("Company");
        AssessmentKit savedKit = kitWithSubjects(List.of(subjectOne, subjectTwo, subjectThree, subjectFour));

        SubjectDslModel dslSubjectOne = domainToDslModel(subjectOne);
        SubjectDslModel dslSubjectTwo = domainToDslModel(subjectTwo);

        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .subjects(List.of(dslSubjectOne, dslSubjectTwo))
            .build();

        Notification notification = validator.validate(savedKit, dslKit);

        assertThat(notification)
            .returns(true, Notification::hasErrors)
            .extracting(Notification::getErrors, as(COLLECTION))
            .singleElement()
            .isInstanceOfSatisfying(InvalidDeletionError.class, x -> {
                assertThat(x.fieldName()).isEqualTo(SUBJECT);
                assertThat(x.deletedItems()).contains(subjectThree.getCode());
                assertThat(x.deletedItems()).contains(subjectFour.getCode());
            });
    }
}
