package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessmentresult.CalculateAssessmentResultUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadCalculateInfoPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.UpdateCalculateResultPort;
import org.flickit.flickitassessmentcore.domain.calculate.AssessmentResult;
import org.flickit.flickitassessmentcore.domain.calculate.MaturityLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CalculateAssessmentResultService implements CalculateAssessmentResultUseCase {

    private final LoadCalculateInfoPort loadCalculateInfoPort;
    private final UpdateCalculateResultPort updateCalculateResultPort;

    @Override
    public Result calculateMaturityLevel(Param param) {
        AssessmentResult assessmentResult = loadCalculateInfoPort.load(param.getAssessmentId());

        MaturityLevel calcResult = assessmentResult.calculate();
        assessmentResult.setMaturityLevel(calcResult);
        assessmentResult.setValid(true);

        updateCalculateResultPort.updateCalculatedResult(assessmentResult);

        return new Result(calcResult);
    }
}
