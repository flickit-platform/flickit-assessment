package org.flickit.assessment.core.application.service.insight.subject;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentMode;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.core.application.domain.insight.SubjectInsight;
import org.flickit.assessment.core.application.port.out.maturitylevel.CountMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.LoadSubjectValuePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static org.flickit.assessment.core.common.ErrorMessageKey.SUBJECT_NOT_FOUND;
import static org.flickit.assessment.core.common.MessageKey.QUICK_ASSESSMENT_SUBJECT_DEFAULT_INSIGHT;
import static org.flickit.assessment.core.common.MessageKey.SUBJECT_DEFAULT_INSIGHT;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CreateSubjectInsightsHelper {

    private final LoadSubjectPort loadSubjectPort;
    private final LoadSubjectValuePort loadSubjectValuePort;
    private final CountMaturityLevelsPort countMaturityLevelsPort;

    public SubjectInsight createSubjectInsight(SubjectInsightParam param) {
        var subject = loadSubjectPort.loadByIdAndKitVersionId(param.subjectId(), param.assessmentResult().getKitVersionId())
            .orElseThrow(() -> new ResourceNotFoundException(SUBJECT_NOT_FOUND));
        var subjectValues = loadSubjectValuePort.load(param.assessmentResult().getId(), subject.getId());
        int maturityLevelsSize = countMaturityLevelsPort.count(param.assessmentResult().getKitVersionId());
        var assessmentMode = param.assessmentResult.getAssessment().getMode();
        var defaultInsight = buildSubjectInsight(assessmentMode, subjectValues, maturityLevelsSize, param.locale);

        return new SubjectInsight(param.assessmentResult().getId(),
            subject.getId(),
            defaultInsight,
            LocalDateTime.now(),
            LocalDateTime.now(),
            null,
            false);
    }

    @Builder
    public record SubjectInsightParam(AssessmentResult assessmentResult,
                                      Long subjectId,
                                      Locale locale) {
    }

    public List<SubjectInsight> createSubjectInsights(SubjectInsightsParam param) {
        var subjectValues = loadSubjectValuePort.loadAll(param.assessmentResult().getId(), param.subjectIds());
        int maturityLevelsSize = countMaturityLevelsPort.count(param.assessmentResult().getKitVersionId());
        var assessmentMode = param.assessmentResult.getAssessment().getMode();

        return subjectValues.stream()
            .map(sv -> new SubjectInsight(param.assessmentResult().getId(),
                sv.getSubject().getId(),
                buildSubjectInsight(assessmentMode, sv, maturityLevelsSize, param.locale),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                false))
            .toList();
    }

    @Builder
    public record SubjectInsightsParam(AssessmentResult assessmentResult,
                                       Collection<Long> subjectIds,
                                       Locale locale) {
    }

    private String buildSubjectInsight(AssessmentMode mode, SubjectValue subjectValues, int maturityLevelsSize, Locale locale) {
        return mode == AssessmentMode.ADVANCED
            ? buildAdvancedAssessmentDefaultInsight(subjectValues, maturityLevelsSize, locale)
            : buildQuickAssessmentDefaultInsight(subjectValues, maturityLevelsSize, locale);
    }

    String buildAdvancedAssessmentDefaultInsight(SubjectValue subjectValue, int maturityLevelsSize, Locale locale) {
        return MessageBundle.message(SUBJECT_DEFAULT_INSIGHT,
            locale,
            subjectValue.getSubject().getTitle(),
            subjectValue.getSubject().getDescription(),
            subjectValue.getConfidenceValue() != null ? (int) Math.ceil(subjectValue.getConfidenceValue()) : 0,
            subjectValue.getSubject().getTitle(),
            subjectValue.getMaturityLevel().getIndex(),
            maturityLevelsSize,
            subjectValue.getMaturityLevel().getTitle(),
            subjectValue.getSubject().getAttributes().size(),
            subjectValue.getSubject().getTitle());
    }

    String buildQuickAssessmentDefaultInsight(SubjectValue subjectValue, int maturityLevelsSize, Locale locale) {
        return MessageBundle.message(QUICK_ASSESSMENT_SUBJECT_DEFAULT_INSIGHT,
            locale,
            subjectValue.getSubject().getTitle(),
            subjectValue.getSubject().getDescription(),
            subjectValue.getSubject().getTitle(),
            subjectValue.getMaturityLevel().getIndex(),
            maturityLevelsSize,
            subjectValue.getMaturityLevel().getTitle(),
            subjectValue.getSubject().getAttributes().size(),
            subjectValue.getSubject().getTitle());
    }
}
