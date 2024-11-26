package org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.impl;

import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AttributeDslModel;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;
import org.flickit.assessment.kit.application.port.out.attribute.CreateAttributePort;
import org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext;
import org.flickit.assessment.kit.test.fixture.application.AttributeMother;
import org.flickit.assessment.kit.test.fixture.application.SubjectMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.AttributeDslModelMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.SubjectDslModelMother;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext.KEY_ATTRIBUTES;
import static org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext.KEY_SUBJECTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttributeCreateKitPersisterTest {

    private static final Long KIT_ID = 1L;
    private static final UUID CURRENT_USER_ID = UUID.randomUUID();
    @InjectMocks
    private AttributeCreateKitPersister persister;
    @Mock
    private CreateAttributePort createAttributePort;

    @Test
    void testOrder() {
        Assertions.assertEquals(4, persister.order());
    }

    @Test
    void testPersist_ValidInputs_SaveAttribute() {
        Attribute attrOne = AttributeMother.attributeWithTitle("attr1");
        Attribute attrTwo = AttributeMother.attributeWithTitle("attr2");
        Subject subject = SubjectMother.subjectWithAttributes("subject1", Arrays.asList(attrOne, attrTwo));

        SubjectDslModel subjectDslModel = SubjectDslModelMother.domainToDslModel(subject);
        AttributeDslModel dslAttrOne = AttributeDslModelMother.domainToDslModel(attrOne, e -> e.subjectCode(subject.getCode()));
        AttributeDslModel dslAttrTwo = AttributeDslModelMother.domainToDslModel(attrTwo, e -> e.subjectCode(subject.getCode()));
        AssessmentKitDslModel dslModel = AssessmentKitDslModel.builder()
            .subjects(List.of(subjectDslModel))
            .attributes(List.of(dslAttrOne, dslAttrTwo))
            .build();
        CreateKitPersisterContext context = new CreateKitPersisterContext();
        context.put(KEY_SUBJECTS, Map.of(subject.getCode(), subject.getId()));

        Attribute attrOneNoId = new Attribute(null, attrOne.getCode(), attrOne.getTitle(), attrOne.getIndex(), attrOne.getDescription(), attrOne.getWeight(), attrOne.getCreationTime(), attrOne.getLastModificationTime(), CURRENT_USER_ID, CURRENT_USER_ID);
        Attribute attrTwoNoId = new Attribute(null, attrTwo.getCode(), attrTwo.getTitle(), attrTwo.getIndex(), attrTwo.getDescription(), attrTwo.getWeight(), attrTwo.getCreationTime(), attrTwo.getLastModificationTime(), CURRENT_USER_ID, CURRENT_USER_ID);
        when(createAttributePort.persist(attrOneNoId, subject.getId(), KIT_ID)).thenReturn(attrOne.getId());
        when(createAttributePort.persist(attrTwoNoId, subject.getId(), KIT_ID)).thenReturn(attrTwo.getId());

        persister.persist(context, dslModel, KIT_ID, CURRENT_USER_ID);

        Map<String, Long> attributes = context.get(KEY_ATTRIBUTES);
        assertEquals(2, attributes.size());
    }
}
