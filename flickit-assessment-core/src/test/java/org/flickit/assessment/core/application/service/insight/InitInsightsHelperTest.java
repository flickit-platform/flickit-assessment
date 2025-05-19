package org.flickit.assessment.core.application.service.insight;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.Subject;
import org.flickit.assessment.core.application.domain.insight.AssessmentInsight;
import org.flickit.assessment.core.application.domain.insight.AttributeInsight;
import org.flickit.assessment.core.application.domain.insight.SubjectInsight;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.CreateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.CreateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.LoadAttributeInsightsPort;
import org.flickit.assessment.core.application.port.out.insight.subject.CreateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.insight.subject.LoadSubjectInsightsPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.core.application.service.insight.assessment.CreateAssessmentInsightHelper;
import org.flickit.assessment.core.application.service.insight.attribute.CreateAttributeAiInsightHelper;
import org.flickit.assessment.core.application.service.insight.subject.CreateSubjectInsightsHelper;
import org.flickit.assessment.core.test.fixture.application.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.core.test.fixture.application.AttributeInsightMother.aiInsightWithTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(MockitoExtension.class)
class InitInsightsHelperTest {

    @InjectMocks
    private InitInsightsHelper helper;

    @Mock
    private LoadAttributesPort loadAttributesPort;

    @Mock
    private LoadAttributeInsightsPort loadAttributeInsightsPort;

    @Mock
    private CreateAttributeAiInsightHelper createAttributeAiInsightHelper;

    @Mock
    private CreateAttributeInsightPort createAttributeInsightPort;

    @Mock
    private LoadSubjectsPort loadSubjectsPort;

    @Mock
    private LoadSubjectInsightsPort loadSubjectInsightsPort;

    @Mock
    private CreateSubjectInsightsHelper createSubjectInsightsHelper;

    @Mock
    private CreateSubjectInsightPort createSubjectInsightPort;

    @Mock
    private LoadAssessmentInsightPort loadAssessmentInsightPort;

    @Mock
    private CreateAssessmentInsightHelper createAssessmentInsightHelper;

    @Mock
    private CreateAssessmentInsightPort createAssessmentInsightPort;

    @Captor
    private ArgumentCaptor<CreateAttributeAiInsightHelper.AttributeInsightsParam> attributeHelperParamCaptor;

    @Captor
    private ArgumentCaptor<CreateSubjectInsightsHelper.SubjectInsightsParam> subjectHelperParamCaptor;

    @Captor
    private ArgumentCaptor<List<AttributeInsight>> attributeInsightCaptor;

    @Captor
    private ArgumentCaptor<List<SubjectInsight>> subjectInsightCaptor;

    private final AssessmentResult assessmentResult = AssessmentResultMother.validResultWithLanguage(KitLanguage.EN, KitLanguage.FA);
    private final Locale locale = Locale.of(assessmentResult.getLanguage().getCode());
    private final LoadAttributesPort.Result attribute = createAttribute();
    private final Subject subject = SubjectMother.subjectWithWeightAndAttributes(1, null);
    private final UUID assessmentId = assessmentResult.getAssessment().getId();

    @Test
    void testInitInsightsHelper_whenOneAttributeInsightIsMissing_thenCreateAndPersistAttributeInsights() {
        var newAttributeInsight = aiInsightWithTime(LocalDateTime.now());

        when(loadAttributesPort.loadAll(assessmentId)).thenReturn(List.of(attribute));
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId())).thenReturn(List.of());
        when(createAttributeAiInsightHelper.createAttributeAiInsights(attributeHelperParamCaptor.capture()))
            .thenReturn(List.of(newAttributeInsight));
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(assessmentResult.getKitVersionId()))
            .thenReturn(List.of());
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId()))
            .thenReturn(List.of());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId()))
            .thenReturn(Optional.of(AssessmentInsightMother.createSimpleAssessmentInsight()));

        helper.initInsights(assessmentResult, locale);

        assertEquals(assessmentResult, attributeHelperParamCaptor.getValue().assessmentResult());
        assertEquals(List.of(attribute.id()), attributeHelperParamCaptor.getValue().attributeIds());
        assertEquals(locale, attributeHelperParamCaptor.getValue().locale());

        verify(createAttributeInsightPort).persistAll(attributeInsightCaptor.capture());
        assertEquals(newAttributeInsight, attributeInsightCaptor.getValue().getFirst());

        verifyNoInteractions(createSubjectInsightsHelper,
            createSubjectInsightPort,
            createAssessmentInsightPort);
    }

    @Test
    void testInitInsightsHelper_whenOneSubjectInsightIsMissing_thenCreateAndPersistSubjectInsights() {
        var newSubjectInsight = SubjectInsightMother.defaultSubjectInsight();

        when(loadAttributesPort.loadAll(assessmentId)).thenReturn(List.of(attribute));
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId()))
            .thenReturn(List.of(AttributeInsightMother.aiInsightWithAttributeId(attribute.id())));
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(assessmentResult.getKitVersionId()))
            .thenReturn(List.of(subject));
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of());
        when(createSubjectInsightsHelper.createSubjectInsights(subjectHelperParamCaptor.capture()))
            .thenReturn(List.of(newSubjectInsight));
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId()))
            .thenReturn(Optional.of(AssessmentInsightMother.createSimpleAssessmentInsight()));

        helper.initInsights(assessmentResult, locale);

        assertEquals(assessmentResult, subjectHelperParamCaptor.getValue().assessmentResult());
        assertEquals(List.of(subject.getId()), subjectHelperParamCaptor.getValue().subjectIds());
        assertEquals(locale, subjectHelperParamCaptor.getValue().locale());

        verify(createSubjectInsightPort).persistAll(subjectInsightCaptor.capture());
        assertEquals(newSubjectInsight, subjectInsightCaptor.getValue().getFirst());

        verifyNoInteractions(createAttributeAiInsightHelper,
            createAttributeInsightPort,
            createAssessmentInsightPort);
    }

    @Test
    void testInitInsightsHelper_whenAssessmentInsightIsMissing_thenCreateAndPersistAssessmentInsight() {
        var newAssessmentInsight = AssessmentInsightMother.createDefaultInsightWithAssessmentResultId(assessmentResult.getId());

        when(loadAttributesPort.loadAll(assessmentId)).thenReturn(List.of(attribute));
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId()))
            .thenReturn(List.of(AttributeInsightMother.aiInsightWithAttributeId(attribute.id())));
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(assessmentResult.getKitVersionId()))
            .thenReturn(List.of(subject));
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId()))
            .thenReturn(List.of(SubjectInsightMother.subjectInsightWithSubjectId(subject.getId())));
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        when(createAssessmentInsightHelper.createAssessmentInsight(assessmentResult, locale)).thenReturn(newAssessmentInsight);
        when(createAssessmentInsightPort.persist(any())).thenReturn(UUID.randomUUID());

        helper.initInsights(assessmentResult, locale);

        var assessmentInsightCaptor = ArgumentCaptor.forClass(AssessmentInsight.class);
        verify(createAssessmentInsightPort).persist(assessmentInsightCaptor.capture());

        assertEquals(newAssessmentInsight, assessmentInsightCaptor.getValue());

        verifyNoInteractions(createAttributeAiInsightHelper,
            createAttributeInsightPort,
            createSubjectInsightsHelper,
            createSubjectInsightPort);
    }

    @Test
    void testInitInsightsHelper_whenAllInsightsExist_thenDoNothing() {
        var existingAttributeInsight = AttributeInsightMother.aiInsightWithAttributeId(attribute.id());
        var existingSubjectInsight = SubjectInsightMother.subjectInsightWithSubjectId(subject.getId());
        var existingAssessmentInsight = AssessmentInsightMother.createSimpleAssessmentInsight();

        when(loadAttributesPort.loadAll(assessmentId)).thenReturn(List.of(attribute));
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId()))
            .thenReturn(List.of(existingAttributeInsight));
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(assessmentResult.getKitVersionId()))
            .thenReturn(List.of(subject));
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId()))
            .thenReturn(List.of(existingSubjectInsight));
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId()))
            .thenReturn(Optional.of(existingAssessmentInsight));

        helper.initInsights(assessmentResult, locale);

        verifyNoInteractions(
            createAttributeAiInsightHelper,
            createAttributeInsightPort,
            createSubjectInsightsHelper,
            createSubjectInsightPort,
            createAssessmentInsightHelper,
            createAssessmentInsightPort
        );
    }


    private static LoadAttributesPort.Result createAttribute() {
        return new LoadAttributesPort.Result(1769L,
            "Software Reliability" + 13,
            "How?",
            13,
            3,
            11.22,
            new LoadAttributesPort.MaturityLevel(1991L,
                "Unprepared" + 13,
                "causing frequent issues and inefficiencies. " + 13, 13, 4),
            new LoadAttributesPort.Subject(464L, "Software" + 13));
    }
}

