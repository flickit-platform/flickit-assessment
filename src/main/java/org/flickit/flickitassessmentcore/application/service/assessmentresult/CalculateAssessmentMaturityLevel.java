package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.flickit.flickitassessmentcore.common.ErrorMessageKey;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;
import org.flickit.flickitassessmentcore.domain.SubjectValue;
import org.springframework.stereotype.Component;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Component
public class CalculateAssessmentMaturityLevel {

    private final LoadMaturityLevelByKitPort loadMaturityLevelByKitPort;

    public MaturityLevel calculate(List<SubjectValue> subjectValues, Long assessmentKitId) {
        List<MaturityLevel> maturityLevels = loadMaturityLevelByKitPort.loadByKitId(assessmentKitId).maturityLevels();
        long mean = calculateMeanOfSubjectMaturityLevels(subjectValues);
        return findMaturityLevelByValue(mean, maturityLevels);
    }

    private MaturityLevel findMaturityLevelByValue(long mean, List<MaturityLevel> maturityLevels) {
        for (MaturityLevel ml : maturityLevels) {
            if (ml.getValue() == mean) {
                return ml;
            }
        }
        throw new ResourceNotFoundException(ErrorMessageKey.CALCULATE_MATURITY_LEVEL_MATURITY_LEVEL_NOT_FOUND_MESSAGE);
    }

    private long calculateMeanOfSubjectMaturityLevels(List<SubjectValue> subjectValues) {
        double sum = 0;
        for (SubjectValue subjectValue : subjectValues) {
            sum += subjectValue.getMaturityLevel().getValue();
        }
        return Math.round(sum / subjectValues.size());
    }
}
