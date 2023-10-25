package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CalculateAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.UpdateAssessmentByIdPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadCalculateInfoPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.UpdateCalculatedResultPort;
import org.flickit.flickitassessmentcore.application.domain.AssessmentResult;
import org.flickit.flickitassessmentcore.application.domain.MaturityLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class CalculateAssessmentService implements CalculateAssessmentUseCase {

    private final LoadCalculateInfoPort loadCalculateInfoPort;
    private final UpdateCalculatedResultPort updateCalculatedResultPort;
    private final UpdateAssessmentByIdPort updateAssessmentByIdPort;

    @Override
    public Result calculateMaturityLevel(Param param) {
        AssessmentResult assessmentResult = loadCalculateInfoPort.load(param.getAssessmentId());

        MaturityLevel calcResult = assessmentResult.calculate();
        assessmentResult.setMaturityLevel(calcResult);
        assessmentResult.setValid(true);
        assessmentResult.setLastModificationTime(LocalDateTime.now());

        updateCalculatedResultPort.updateCalculatedResult(assessmentResult);

        UpdateAssessmentByIdPort.Param updateAssessmentById = new UpdateAssessmentByIdPort.Param(
            param.getAssessmentId(),
            assessmentResult.getLastModificationTime());
        updateAssessmentByIdPort.updateById(updateAssessmentById);


        return new Result(calcResult);
    }
}
