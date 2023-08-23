package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.UpdateAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.SaveAssessmentPort;
import org.flickit.flickitassessmentcore.domain.Assessment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateAssessmentService implements UpdateAssessmentUseCase {

    private final LoadAssessmentPort loadAssessmentPort;
    private final SaveAssessmentPort saveAssessmentPort;

    @Override
    public Result updateAssessment(Param param) {
        Assessment assessment = loadAssessmentPort.loadAssessment(param.getId()).assessment();
        Assessment newAssessment = new Assessment(
            assessment.getId(),
            assessment.getCode(),
            param.getTitle(),
            assessment.getAssessmentKit(),
            param.getColorId(),
            assessment.getSpaceId(),
            assessment.getCreationTime(),
            LocalDateTime.now()
        );
        UUID id = saveAssessmentPort.saveAssessment(new SaveAssessmentPort.Param(newAssessment)).id();
        return new Result(id);
    }
}
