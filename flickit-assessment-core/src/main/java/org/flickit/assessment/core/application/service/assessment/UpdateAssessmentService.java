package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.assessment.UpdateAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.core.application.domain.Assessment.generateSlugCode;
import static org.flickit.assessment.core.application.domain.AssessmentColor.getValidId;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateAssessmentService implements UpdateAssessmentUseCase {

    private final UpdateAssessmentPort updateAssessmentPort;

    @Override
    public Result updateAssessment(Param param) {
        String code = generateSlugCode(param.getTitle());
        LocalDateTime lastModificationTime = LocalDateTime.now();
        UpdateAssessmentPort.AllParam updateParam = new UpdateAssessmentPort.AllParam(
            param.getId(),
            param.getTitle(),
            code,
            getValidId(param.getColorId()),
            lastModificationTime,
            param.getLastModifiedBy());

        return new Result(updateAssessmentPort.update(updateParam).id());
    }
}
