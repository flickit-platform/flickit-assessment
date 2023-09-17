package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.UpdateAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.UpdateAssessmentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.flickitassessmentcore.application.domain.Assessment.generateSlugCode;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateAssessmentService implements UpdateAssessmentUseCase {

    private final UpdateAssessmentPort updateAssessmentPort;

    @Override
    public Result updateAssessment(Param param) {
        String code = generateSlugCode(param.getTitle());
        LocalDateTime lastModificationTime = LocalDateTime.now();
        UpdateAssessmentPort.Param updateParam = new UpdateAssessmentPort.Param(
            param.getId(),
            param.getTitle(),
            code,
            param.getColorId(),
            lastModificationTime);

        return new Result(updateAssessmentPort.update(updateParam).id());
    }
}
