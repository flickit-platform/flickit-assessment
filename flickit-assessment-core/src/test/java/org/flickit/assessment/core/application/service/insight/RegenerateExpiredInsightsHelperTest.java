package org.flickit.assessment.core.application.service.insight;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.insight.AssessmentInsight;
import org.flickit.assessment.core.application.domain.insight.SubjectInsight;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.UpdateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.LoadAttributeInsightsPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.UpdateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.insight.subject.LoadSubjectInsightsPort;
import org.flickit.assessment.core.application.port.out.insight.subject.UpdateSubjectInsightPort;
import org.flickit.assessment.core.application.service.insight.assessment.CreateAssessmentInsightHelper;
import org.flickit.assessment.core.application.service.insight.attribute.CreateAttributeAiInsightHelper;
import org.flickit.assessment.core.application.service.insight.subject.CreateSubjectInsightsHelper;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
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

import static org.flickit.assessment.core.test.fixture.application.AssessmentInsightMother.createDefaultInsightWithAssessmentResultId;
import static org.flickit.assessment.core.test.fixture.application.AssessmentInsightMother.createDefaultInsightWithTimesAndApprove;
import static org.flickit.assessment.core.test.fixture.application.AttributeInsightMother.aiInsightWithAttributeId;
import static org.flickit.assessment.core.test.fixture.application.AttributeInsightMother.aiInsightWithTime;
import static org.flickit.assessment.core.test.fixture.application.SubjectInsightMother.defaultSubjectInsight;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegenerateExpiredInsightsHelperTest {

    @InjectMocks
    private RegenerateExpiredInsightsHelper helper;

    @Mock
    private UpdateAttributeInsightPort updateAttributeInsightPort;

    @Mock
    private UpdateSubjectInsightPort updateSubjectInsightPort;

    @Mock
    private UpdateAssessmentInsightPort updateAssessmentInsightPort;

    @Mock
    private LoadAttributeInsightsPort loadAttributeInsightsPort;

    @Mock
    private CreateAttributeAiInsightHelper createAttributeAiInsightHelper;

    @Mock
    private CreateSubjectInsightsHelper createSubjectInsightsHelper;

    @Mock
    private LoadSubjectInsightsPort loadSubjectInsightsPort;

    @Mock
    private LoadAssessmentInsightPort loadAssessmentInsightPort;

    @Mock
    private CreateAssessmentInsightHelper createAssessmentInsightHelper;

    @Captor
    private ArgumentCaptor<CreateAttributeAiInsightHelper.AttributeInsightsParam> attributeHelperParamCaptor;

    @Captor
    private ArgumentCaptor<List<UpdateAttributeInsightPort.AiParam>> attributeInsightCaptor;

    @Captor
    private ArgumentCaptor<CreateSubjectInsightsHelper.SubjectInsightsParam> subjectHelperParamCaptor;

    @Captor
    private ArgumentCaptor<List<SubjectInsight>> subjectInsightCaptor;

    private final AssessmentResult assessmentResult = AssessmentResultMother.validResultWithLanguage(KitLanguage.FA, KitLanguage.EN);
    private final Locale locale = Locale.of(assessmentResult.getLanguage().getCode());

    @Test
    void testRegenerateExpiredInsights_whenOneAttributeInsightIsExpired_thenCreateAndUpdate() {
        var expiredInsight = aiInsightWithTime(LocalDateTime.now().minusDays(1));
        var newInsight = aiInsightWithAttributeId(expiredInsight.getAttributeId());

        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId())).thenReturn(List.of(expiredInsight));
        when(createAttributeAiInsightHelper.createAttributeAiInsights(attributeHelperParamCaptor.capture()))
            .thenReturn(List.of(newInsight));

        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());

        helper.regenerateExpiredInsights(assessmentResult, locale);

        var param = attributeHelperParamCaptor.getValue();
        assertEquals(assessmentResult, param.assessmentResult());
        assertEquals(List.of(expiredInsight.getAttributeId()), param.attributeIds());
        assertEquals(locale, param.locale());

        verify(updateAttributeInsightPort).updateAiInsights(attributeInsightCaptor.capture());
        var aiParam = attributeInsightCaptor.getValue().getFirst();
        assertEquals(newInsight.getAttributeId(), aiParam.attributeId());
        assertEquals(newInsight.getAssessmentResultId(), aiParam.assessmentResultId());
        assertEquals(newInsight.getAiInsight(), aiParam.aiInsight());
        assertEquals(newInsight.getAiInsightTime(), aiParam.aiInsightTime());
        assertEquals(newInsight.getAiInputPath(), aiParam.aiInputPath());
        assertEquals(newInsight.isApproved(), aiParam.isApproved());
        assertEquals(newInsight.getLastModificationTime(), aiParam.lastModificationTime());

        verifyNoInteractions(updateSubjectInsightPort, updateAssessmentInsightPort);
    }

    @Test
    void testRegenerateExpiredInsights_whenOneSubjectInsightIsExpired_thenCreateAndUpdate() {
        var expiredSubject = defaultSubjectInsight(LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1), false);
        var newSubject = defaultSubjectInsight();

        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId()))
            .thenReturn(List.of());

        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId()))
            .thenReturn(List.of(expiredSubject));
        when(createSubjectInsightsHelper.createSubjectInsights(subjectHelperParamCaptor.capture()))
            .thenReturn(List.of(newSubject));

        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());

        helper.regenerateExpiredInsights(assessmentResult, locale);

        var param = subjectHelperParamCaptor.getValue();
        assertEquals(assessmentResult, param.assessmentResult());
        assertEquals(List.of(expiredSubject.getSubjectId()), param.subjectIds());
        assertEquals(locale, param.locale());

        verify(updateSubjectInsightPort).updateAll(subjectInsightCaptor.capture());
        assertEquals(newSubject, subjectInsightCaptor.getValue().getFirst());

        verifyNoInteractions(updateAttributeInsightPort, updateAssessmentInsightPort);
    }

    @Test
    void testRegenerateExpiredInsights_whenAssessmentInsightIsExpired_thenCreateAndUpdate() {
        var expiredInsight = createDefaultInsightWithTimesAndApprove(LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1), false);
        var newInsight = createDefaultInsightWithAssessmentResultId(assessmentResult.getId());

        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId())).thenReturn(List.of());
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId()))
            .thenReturn(Optional.of(expiredInsight));
        when(createAssessmentInsightHelper.createAssessmentInsight(assessmentResult, locale))
            .thenReturn(newInsight);

        helper.regenerateExpiredInsights(assessmentResult, locale);

        var insightCaptor = ArgumentCaptor.forClass(AssessmentInsight.class);
        verify(updateAssessmentInsightPort).updateInsight(insightCaptor.capture());

        var updated = insightCaptor.getValue();
        assertEquals(expiredInsight.getId(), updated.getId());
        assertEquals(newInsight.getAssessmentResultId(), updated.getAssessmentResultId());
        assertEquals(newInsight.getInsight(), updated.getInsight());
        assertEquals(newInsight.getInsightTime(), updated.getInsightTime());
        assertEquals(newInsight.getLastModificationTime(), updated.getLastModificationTime());
        assertEquals(newInsight.getInsightBy(), updated.getInsightBy());
        assertEquals(newInsight.isApproved(), updated.isApproved());

        verifyNoInteractions(updateAttributeInsightPort, updateSubjectInsightPort);
    }

    @Test
    void testRegenerateExpiredInsights_whenNothingIsExpired_thenNoUpdateCalls() {
        var freshInsight = aiInsightWithTime(LocalDateTime.now().plusDays(1));
        var freshSubject = defaultSubjectInsight(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1), true);
        var freshAssessment = createDefaultInsightWithTimesAndApprove(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1), true);

        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId())).thenReturn(List.of(freshInsight));
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of(freshSubject));
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(freshAssessment));

        helper.regenerateExpiredInsights(assessmentResult, locale);

        verifyNoInteractions(updateAttributeInsightPort, updateSubjectInsightPort, updateAssessmentInsightPort,
            createAttributeAiInsightHelper, createSubjectInsightsHelper, createAssessmentInsightHelper);
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
