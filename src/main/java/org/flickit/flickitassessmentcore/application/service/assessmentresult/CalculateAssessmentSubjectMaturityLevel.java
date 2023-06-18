package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.flickitassessmentcore.application.port.in.assessmentresult.CalculateAssessmentSubjectMaturityLevelUseCase;
import org.flickit.flickitassessmentcore.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattribute.LoadQualityAttributeBySubPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.LoadQAValuesByQAIdsPort;
import org.flickit.flickitassessmentcore.application.service.exception.NoMaturityLevelFound;
import org.flickit.flickitassessmentcore.domain.*;

import java.util.*;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Slf4j
public class CalculateAssessmentSubjectMaturityLevel implements CalculateAssessmentSubjectMaturityLevelUseCase {

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
        MaturityLevel subMaturityLevel = findMaturityLevelByValue(weightedMean, maturityLevels, subject.getId());
        return new AssessmentSubjectValue(
            UUID.randomUUID(),
            subject,
            subMaturityLevel
        );
    }

    private MaturityLevel findMaturityLevelByValue(long weightedMean, Set<MaturityLevel> maturityLevels, Long subId) {
        for (MaturityLevel ml : maturityLevels) {
            if (ml.getValue() == weightedMean) {
                return ml;
            }
        }
        throw new NoMaturityLevelFound("Can't calculate Maturity Level for Subject with id [" + subId + "].");
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
