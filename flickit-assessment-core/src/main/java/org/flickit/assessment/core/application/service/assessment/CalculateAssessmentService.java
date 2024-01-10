package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import io.sentry.Sentry;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.port.in.assessment.CalculateAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadCalculateInfoPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedResultPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class CalculateAssessmentService implements CalculateAssessmentUseCase {

    private final LoadCalculateInfoPort loadCalculateInfoPort;
    private final UpdateCalculatedResultPort updateCalculatedResultPort;
    private final UpdateAssessmentPort updateAssessmentPort;

    @Override
    public Result calculateMaturityLevel(Param param) {

        try {
            throw new Exception("This is a test.");
        } catch (Exception e) {
            Sentry.captureException(e);
        }
        if (true)
            throw new RuntimeException("NOO");
        AssessmentResult assessmentResult = loadCalculateInfoPort.load(param.getAssessmentId());

        MaturityLevel calcResult = assessmentResult.calculate();
        assessmentResult.setMaturityLevel(calcResult);
        assessmentResult.setCalculateValid(true);
        assessmentResult.setLastModificationTime(LocalDateTime.now());

        updateCalculatedResultPort.updateCalculatedResult(assessmentResult);

        updateAssessmentPort.updateLastModificationTime(param.getAssessmentId(), assessmentResult.getLastModificationTime());


        return new Result(calcResult);
    }
}
