package org.flickit.assessment.core.application.service.insight;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.Subject;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InitAssessmentInsightsHelper {

    private final LoadAttributesPort loadAttributesPort;
    private final LoadAttributeInsightsPort loadAttributeInsightsPort;
    private final CreateAttributeAiInsightHelper createAttributeAiInsightHelper;
    private final CreateAttributeInsightPort createAttributeInsightPort;
    private final LoadSubjectsPort loadSubjectsPort;
    private final CreateSubjectInsightsHelper createSubjectInsightsHelper;
    private final LoadSubjectInsightsPort loadSubjectInsightsPort;
    private final CreateSubjectInsightPort createSubjectInsightPort;
    private final LoadAssessmentInsightPort loadAssessmentInsightPort;
    private final CreateAssessmentInsightHelper createAssessmentInsightHelper;
    private final CreateAssessmentInsightPort createAssessmentInsightPort;

    public void initInsights(AssessmentResult assessmentResult, Locale locale) {
        initAttributesInsight(assessmentResult.getAssessment().getId(), assessmentResult, locale);
        initSubjectsInsight(assessmentResult, locale);
        initAssessmentInsight(assessmentResult, locale);
    }

    private void initAttributesInsight(UUID assessmentId, AssessmentResult assessmentResult, Locale locale) {
        var attributeIds = loadAttributesPort.loadAll(assessmentId).stream()
            .map(LoadAttributesPort.Result::id)
            .collect(toList());
        var attributeInsightIds = loadAttributeInsightsPort.loadInsights(assessmentResult.getId()).stream()
            .map(AttributeInsight::getAttributeId)
            .toList();
        attributeIds.removeAll(attributeInsightIds);
        if (!attributeIds.isEmpty()) {
            log.info("Creating attributes insights for attributeIds= [{}] of assessmentId=[{}]", attributeIds, assessmentId);
            var attributeAiInsights = createAttributeAiInsightHelper
                .createAttributeAiInsights(new CreateAttributeAiInsightHelper.AttributeInsightsParam(assessmentResult, attributeIds, locale));
            createAttributeInsightPort.persistAll(attributeAiInsights);
        }
    }

    private void initSubjectsInsight(AssessmentResult assessmentResult, Locale locale) {
        var subjectIds = loadSubjectsPort.loadByKitVersionIdWithAttributes(assessmentResult.getKitVersionId()).stream()
            .map(Subject::getId)
            .collect(toList());
        var subjectInsightIds = loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId()).stream()
            .map(SubjectInsight::getSubjectId)
            .toList();
        subjectIds.removeAll(subjectInsightIds);
        if (!subjectIds.isEmpty()) {
            log.info("Creating subjects insights for subjectIds= [{}] of assessmentId=[{}]", subjectIds, assessmentResult.getId());
            var subjectInsights = createSubjectInsightsHelper
                .createSubjectInsights(new CreateSubjectInsightsHelper.SubjectInsightsParam(assessmentResult, subjectIds, locale));
            createSubjectInsightPort.persistAll(subjectInsights);
        }
    }

    private void initAssessmentInsight(AssessmentResult assessmentResult, Locale locale) {
        var loadedAssessmentInsight = loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId());
        if (loadedAssessmentInsight.isEmpty()) {
            log.info("Creating assessment insight for assessmentId=[{}]", assessmentResult.getAssessment().getId());
            var assessmentInsight = createAssessmentInsightHelper.createAssessmentInsight(assessmentResult, locale);
            createAssessmentInsightPort.persist(assessmentInsight);
        }
    }
}
