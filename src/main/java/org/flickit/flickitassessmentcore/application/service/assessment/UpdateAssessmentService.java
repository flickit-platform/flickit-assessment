package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.UpdateAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.UpdateAssessmentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateAssessmentService implements UpdateAssessmentUseCase {

    private final UpdateAssessmentPort updateAssessmentPort;

    @Override
    public Result updateAssessment(Param param) {
        UpdateAssessmentPort.Param updateParam = new UpdateAssessmentPort.Param(
            param.getId(),
            param.getTitle(),
            param.getColorId(),
            LocalDateTime.now());

        return new Result(updateAssessmentPort.update(updateParam).id());
    }
}
