package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AttributeDslModel;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;
import org.flickit.assessment.kit.application.port.out.attribute.UpdateAttributePort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterResult;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.AttributeMother;
import org.flickit.assessment.kit.test.fixture.application.SubjectMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.AttributeDslModelMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.SubjectDslModelMother;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext.KEY_ATTRIBUTES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class AttributeUpdateKitPersisterTest {

    @InjectMocks
    private AttributeUpdateKitPersister persister;

    @Captor
    ArgumentCaptor<UpdateAttributePort.Param> captor;

    @Mock
    private UpdateAttributePort updateAttributePort;

    @Test
    void testOrder() {
        Assertions.assertEquals(4, persister.order());
    }


    @Test
    void testPersist_SameAttributeCodesWithDifferentFields_Update() {
        Attribute attrOne = AttributeMother.attributeWithTitle("attr1");
        Attribute attrTwo = AttributeMother.attributeWithTitle("attr2");
        Subject subject = SubjectMother.subjectWithAttributes("subject1", Arrays.asList(attrOne, attrTwo));
        AssessmentKit savedKit = AssessmentKitMother.kitWithSubjects(List.of(subject));



        SubjectDslModel subjectDslModel = SubjectDslModelMother.domainToDslModel(subject);
        AttributeDslModel dslAttrOne =
            AttributeDslModelMother.domainToDslModel(attrOne, e -> {
                e.title("new title");
                e.subjectCode(subject.getCode());
            });
        AttributeDslModel dslAttrTwo =
            AttributeDslModelMother.domainToDslModel(attrTwo, e -> {
                e.description("new description");
                e.subjectCode(subject.getCode());
                e.weight(2);
            });

        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .subjects(List.of(subjectDslModel))
            .attributes(List.of(dslAttrOne, dslAttrTwo))
            .build();

        Mockito.doNothing().when(updateAttributePort).update(Mockito.any());

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        UpdateKitPersisterResult result = persister.persist(ctx, savedKit, dslKit);

        Mockito.verify(updateAttributePort, Mockito.times(2)).update(captor.capture());
        List<UpdateAttributePort.Param> params = captor.getAllValues();
        UpdateAttributePort.Param firstAttr = params.get(0);
        UpdateAttributePort.Param secondAttr = params.get(1);

        Assertions.assertEquals(attrOne.getId(), firstAttr.id());
        Assertions.assertEquals(dslAttrOne.getTitle(), firstAttr.title());
        Assertions.assertEquals(attrOne.getIndex(), firstAttr.index());
        Assertions.assertEquals(attrOne.getDescription(), firstAttr.description());
        Assertions.assertEquals(attrOne.getWeight(), firstAttr.weight());
        assertThat(firstAttr.lastModificationTime(), lessThanOrEqualTo(LocalDateTime.now()));
        Assertions.assertEquals(subject.getId(), firstAttr.subjectId());

        Assertions.assertEquals(attrTwo.getId(), secondAttr.id());
        Assertions.assertEquals(attrTwo.getTitle(), secondAttr.title());
        Assertions.assertEquals(attrTwo.getIndex(), secondAttr.index());
        Assertions.assertEquals(dslAttrTwo.getDescription(), secondAttr.description());
        Assertions.assertEquals(dslAttrTwo.getWeight(), secondAttr.weight());
        assertThat(secondAttr.lastModificationTime(), lessThanOrEqualTo(LocalDateTime.now()));
        Assertions.assertEquals(subject.getId(), secondAttr.subjectId());

        Assertions.assertTrue(result.shouldInvalidateCalcResult());

        Map<String, Long> codeToIdMap = ctx.get(KEY_ATTRIBUTES);
        assertNotNull(codeToIdMap);
        assertEquals(2, codeToIdMap.keySet().size());
    }
}
