package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.flickit.flickitassessmentcore.common.ErrorMessageKey;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;
import org.flickit.flickitassessmentcore.domain.SubjectValue;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CalculateAssessmentMaturityLevel {

    public MaturityLevel calculate(List<MaturityLevel> maturityLevels, List<SubjectValue> subjectValues) {
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
