package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.flickitassessmentcore.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.flickit.flickitassessmentcore.common.ErrorMessageKey;
import org.flickit.flickitassessmentcore.domain.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Transactional
@RequiredArgsConstructor
@Component
@Slf4j
public class CalculateAssessmentSubjectMaturityLevel {

    private final LoadMaturityLevelByKitPort loadMaturityLevelByKitPort;

    public MaturityLevel calculateAssessmentSubjectMaturityLevel(List<QualityAttributeValue> qualityAttributeValues, Long assessmentKitId) {
        long weightedMean = calculateWeightedMeanOfQAMaturityLevels(qualityAttributeValues);
        Set<MaturityLevel> maturityLevels = loadMaturityLevelByKitPort.loadMaturityLevelByKitId(assessmentKitId);
        MaturityLevel subMaturityLevel = findMaturityLevelByValue(weightedMean, maturityLevels);
        return subMaturityLevel;
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
