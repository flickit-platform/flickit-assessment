package org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.validate.impl;

import org.flickit.assessment.common.exception.api.Notification;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AttributeDslModel;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.AttributeMother;
import org.flickit.assessment.kit.test.fixture.application.SubjectMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.AttributeDslModelMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.SubjectDslModelMother;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class AttributeUpdateValidatorTest {

    @InjectMocks
    private AttributeUpdateValidator validator;

    @Test
    void testValidate_SameAttributeCodesInDbAndDsl_Valid() {
        Attribute attrOne = AttributeMother.attributeWithTitle("attr1");
        Attribute attrTwo = AttributeMother.attributeWithTitle("attr2");
        Subject subject = SubjectMother.subjectWithAttributes("subject1", Arrays.asList(attrOne, attrTwo));
        AssessmentKit savedKit = AssessmentKitMother.kitWithSubjects(List.of(subject));

        AttributeDslModel dslAttrOne =
            AttributeDslModelMother.domainToDslModel(attrOne, e -> e.title("new title"));
        AttributeDslModel dslAttrTwo =
            AttributeDslModelMother.domainToDslModel(attrTwo, e -> e.description("new description"));

        SubjectDslModel subjectDslModel = SubjectDslModelMother.domainToDslModel(subject);

        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .subjects(List.of(subjectDslModel))
            .attributes(List.of(dslAttrOne, dslAttrTwo))
            .build();

        Notification notification = validator.validate(savedKit, dslKit);

        Assertions.assertFalse(notification.hasErrors());

    }

    @Test
    void testValidate_DslHasTwoAttributesLessThanDb_Invalid() {
        Attribute attrOne = AttributeMother.attributeWithTitle("attr1");
        Attribute attrTwo = AttributeMother.attributeWithTitle("attr2");
        Attribute attrThree = AttributeMother.attributeWithTitle("attr3");
        Attribute attrFour = AttributeMother.attributeWithTitle("attr4");
        Subject subject =
            SubjectMother.subjectWithAttributes("subject1", Arrays.asList(attrOne, attrTwo, attrThree, attrFour));
        AssessmentKit savedKit = AssessmentKitMother.kitWithSubjects(List.of(subject));

        AttributeDslModel dslAttrOne =
            AttributeDslModelMother.domainToDslModel(attrOne, e -> e.title("new title"));
        AttributeDslModel dslAttrTwo =
            AttributeDslModelMother.domainToDslModel(attrTwo, e -> e.description("new description"));

        SubjectDslModel subjectDslModel = SubjectDslModelMother.domainToDslModel(subject);

        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .subjects(List.of(subjectDslModel))
            .attributes(List.of(dslAttrOne, dslAttrTwo))
            .build();

        Notification notification = validator.validate(savedKit, dslKit);

        Assertions.assertTrue(notification.hasErrors());
    }
}
