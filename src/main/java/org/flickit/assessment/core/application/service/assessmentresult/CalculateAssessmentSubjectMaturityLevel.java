package org.flickit.assessment.core.application.service.assessmentresult;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
import org.flickit.assessment.core.application.port.out.qualityattribute.LoadQualityAttributeBySubPort;
import org.flickit.assessment.core.application.port.out.qualityattributevalue.LoadQAValuesByQAIdsPort;
import org.flickit.assessment.core.application.service.exception.ResourceNotFoundException;
import org.flickit.assessment.core.common.ErrorMessageKey;
import org.flickit.assessment.core.domain.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Component
@Slf4j
public class CalculateAssessmentSubjectMaturityLevel {

    private final LoadQualityAttributeBySubPort loadQABySubId;
    private final LoadQAValuesByQAIdsPort loadQAValuesByQAIds;
    private final LoadMaturityLevelByKitPort loadMaturityLevelByKitPort;

    public AssessmentSubjectValue calculateAssessmentSubjectMaturityLevel(AssessmentSubject subject) {
        List<QualityAttribute> qualityAttributes = loadQABySubId.loadQABySubId(subject.getId());
        List<QualityAttributeValue> qualityAttributeValues = loadQAValuesByQAIds.loadQAValuesByQAIds(
            qualityAttributes.stream()
                .map(QualityAttribute::getId)
                .collect(Collectors.toSet()));
        long weightedMean = calculateWeightedMeanOfQAMaturityLevels(qualityAttributeValues);
        Set<MaturityLevel> maturityLevels = loadMaturityLevelByKitPort.loadMaturityLevelByKitId(subject.getAssessmentKit().getId());
        MaturityLevel subMaturityLevel = findMaturityLevelByValue(weightedMean, maturityLevels);
        return new AssessmentSubjectValue(
            UUID.randomUUID(),
            subject,
            subMaturityLevel
        );
    }

    private MaturityLevel findMaturityLevelByValue(long weightedMean, Set<MaturityLevel> maturityLevels) {
        for (MaturityLevel ml : maturityLevels) {
            if (ml.getValue() == weightedMean) {
                return ml;
            }
        }
        throw new ResourceNotFoundException(ErrorMessageKey.CALCULATE_MATURITY_LEVEL_MATURITY_LEVEL_NOT_FOUND_MESSAGE);
    }

    private long calculateWeightedMeanOfQAMaturityLevels(List<QualityAttributeValue> qualityAttributeValues) {
        double sum = 0;
        long weight = 0;
        for (QualityAttributeValue qav : qualityAttributeValues) {
            sum += qav.getMaturityLevel().getValue() * qav.getQualityAttribute().getWeight();
            weight += qav.getQualityAttribute().getWeight();
        }

        return Math.round(sum / weight);
    }

}
