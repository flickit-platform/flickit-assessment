package org.flickit.assessment.core.application.service.insight.subject;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentKit;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.core.application.domain.insight.SubjectInsight;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.core.application.port.out.maturitylevel.CountMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.LoadSubjectValuePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_KIT_ID_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.SUBJECT_NOT_FOUND;
import static org.flickit.assessment.core.common.MessageKey.SUBJECT_DEFAULT_INSIGHT;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CreateSubjectInsightsHelper {

    private final LoadSubjectPort loadSubjectPort;
    private final LoadSubjectValuePort loadSubjectValuePort;
    private final CountMaturityLevelsPort countMaturityLevelsPort;
    private final LoadAssessmentKitPort loadAssessmentKitPort;

    public SubjectInsight createSubjectInsight(SubjectInsightParam param) {
        var assessmentResult = param.assessmentResult;
        var kit = loadAssessmentKitPort.loadAssessmentKit(assessmentResult.getAssessment().getAssessmentKit().getId(), null)
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_KIT_ID_NOT_FOUND));
        var translationLanguage = resolveLanguage(kit, assessmentResult);
        var subject = loadSubjectPort.load(param.subjectId(), assessmentResult.getKitVersionId(), translationLanguage)
            .orElseThrow(() -> new ResourceNotFoundException(SUBJECT_NOT_FOUND));
        var subjectValues = loadSubjectValuePort.load(param.assessmentResult().getId(), subject.getId());
        int maturityLevelsSize = countMaturityLevelsPort.count(param.assessmentResult().getKitVersionId());

        Locale locale = translationLanguage != null
            ? Locale.of(translationLanguage.getCode())
            : Locale.of(kit.getLanguage().getCode());

        return new SubjectInsight(param.assessmentResult().getId(),
            subject.getId(),
            buildDefaultInsight(subjectValues, maturityLevelsSize, locale),
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

        return subjectValues.stream()
            .map(sv -> new SubjectInsight(param.assessmentResult().getId(),
                sv.getSubject().getId(),
                buildDefaultInsight(sv, maturityLevelsSize, param.locale()),
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

    private String buildDefaultInsight(SubjectValue subjectValue, int maturityLevelsSize, Locale locale) {
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

    private KitLanguage resolveLanguage(AssessmentKit assessmentKit, AssessmentResult assessmentResult) {
        return Objects.equals(assessmentKit.getLanguage(), assessmentResult.getLanguage())
            ? null
            : assessmentResult.getLanguage();
    }
}
