package org.flickit.flickitassessmentcore.application.service.assessmentsubject;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.flickitassessmentcore.application.port.in.assessmentsubject.CalculateSubjectMaturityLevelCommand;
import org.flickit.flickitassessmentcore.application.port.in.assessmentsubject.CalculateSubjectMaturityLevelUseCase;
import org.flickit.flickitassessmentcore.application.port.out.*;
import org.flickit.flickitassessmentcore.application.service.exception.NoMaturityLevelFound;
import org.flickit.flickitassessmentcore.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class CalculateSubjectMaturityLevelService implements CalculateSubjectMaturityLevelUseCase {

    private final LoadSubjectPort loadSubject;
    private final LoadQABySubIdPort loadQABySubId;
    private final LoadQAValuesByQAIdsPort loadQAValuesByQAIds;
    private final LoadMLByKitPort loadMLByKitPort;

    @Override
    public MaturityLevel calculateSubjectMaturityLevel(CalculateSubjectMaturityLevelCommand command) {
        AssessmentSubject subject = loadSubject.loadSubject(command.getSubId());
        List<QualityAttribute> qualityAttributes = loadQABySubId.loadQABySubId(command.getSubId());
        List<QualityAttributeValue> qualityAttributeValues = loadQAValuesByQAIds.LoadQAValuesByQAIds(
            qualityAttributes.stream()
                .map(QualityAttribute::getId)
                .collect(Collectors.toSet()));
        long weightedMean = calculateWeightedMeanOfQAMaturityLevels(qualityAttributeValues);
        Set<MaturityLevel> maturityLevels = loadMLByKitPort.loadMLByKitId(subject.getAssessmentKit().getId());
        MaturityLevel subMaturityLevel = findMaturityLevelByValue(weightedMean, maturityLevels, command.getSubId());
        return subMaturityLevel;
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
