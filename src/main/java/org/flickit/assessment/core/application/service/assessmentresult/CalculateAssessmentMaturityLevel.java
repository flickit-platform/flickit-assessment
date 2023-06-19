package org.flickit.assessment.core.application.service.assessmentresult;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.core.application.port.in.assessmentresult.CalculateAssessmentMaturityLevelUseCase;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
import org.flickit.assessment.core.application.service.exception.NoMaturityLevelFound;
import org.flickit.assessment.core.domain.Assessment;
import org.flickit.assessment.core.domain.AssessmentSubject;
import org.flickit.assessment.core.domain.AssessmentSubjectValue;
import org.flickit.assessment.core.domain.MaturityLevel;
import org.flickit.assessment.core.domain.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Transactional
@RequiredArgsConstructor
@Component
@Slf4j
public class CalculateAssessmentMaturityLevel implements CalculateAssessmentMaturityLevelUseCase {

    private final LoadMaturityLevelByKitPort loadMaturityLevelByKitPort;

    @Override
    public MaturityLevel calculateAssessmentMaturityLevel(List<AssessmentSubjectValue> subjectValues, Assessment assessment) {
        AssessmentSubject subject = subjectValues.get(0).getAssessmentSubject();
        Set<MaturityLevel> maturityLevels = loadMaturityLevelByKitPort.loadMaturityLevelByKitId(subject.getAssessmentKit().getId());
        long mean = calculateMeanOfSubjectMaturityLevels(subjectValues);
        return findMaturityLevelByValue(mean, maturityLevels, assessment.getId());
    }

    private MaturityLevel findMaturityLevelByValue(long mean, Set<MaturityLevel> maturityLevels, UUID assessmentId) {
        for (MaturityLevel ml : maturityLevels) {
            if (ml.getValue() == mean) {
                return ml;
            }
        }
        throw new NoMaturityLevelFound("Can't calculate Maturity Level for Assessment with id [" + assessmentId + "].");
    }

    private long calculateMeanOfSubjectMaturityLevels(List<AssessmentSubjectValue> subjectValues) {
        double sum = 0;
        for (AssessmentSubjectValue subjectValue : subjectValues) {
            sum += subjectValue.getMaturityLevel().getValue();
        }
        return Math.round(sum / subjectValues.size());
    }
}
