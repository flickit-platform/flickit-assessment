package org.flickit.assessment.core.application.service.insight.subject;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.core.application.domain.insight.SubjectInsight;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.LoadSubjectValuePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static org.flickit.assessment.core.common.MessageKey.SUBJECT_DEFAULT_INSIGHT;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CreateSubjectInsightsHelper {

    private final LoadSubjectValuePort loadSubjectValuePort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;

    public List<SubjectInsight> initSubjectInsights(Param param) {
        var subjectValues = loadSubjectValuePort.loadAll(param.assessmentResult().getId(), param.subjectIds());
        int maturityLevelsSize = loadMaturityLevelsPort.loadByKitVersionId(param.assessmentResult().getKitVersionId()).size();

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
    public record Param(AssessmentResult assessmentResult,
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
}
