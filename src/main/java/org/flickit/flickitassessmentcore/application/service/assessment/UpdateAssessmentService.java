package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.UpdateAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CheckAssessmentExistencePort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.flickitassessmentcore.application.domain.Assessment.generateSlugCode;
import static org.flickit.flickitassessmentcore.application.domain.AssessmentColor.getValidId;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.UPDATE_ASSESSMENT_ID_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateAssessmentService implements UpdateAssessmentUseCase {

    private final UpdateAssessmentPort updateAssessmentPort;
    private final CheckAssessmentExistencePort checkAssessmentExistencePort;

    @Override
    public Result updateAssessment(Param param) {
        if (!checkAssessmentExistencePort.existsById(param.getId()))
            throw new ResourceNotFoundException(UPDATE_ASSESSMENT_ID_NOT_FOUND);
        String code = generateSlugCode(param.getTitle());
        LocalDateTime lastModificationTime = LocalDateTime.now();
        UpdateAssessmentPort.Param updateParam = new UpdateAssessmentPort.Param(
            param.getId(),
            param.getTitle(),
            code,
            getValidId(param.getColorId()),
            lastModificationTime);

        return new Result(updateAssessmentPort.update(updateParam).id());
    }
}
