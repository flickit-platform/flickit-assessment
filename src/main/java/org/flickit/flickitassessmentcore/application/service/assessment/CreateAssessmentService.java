package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.AssessmentColorDto;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentCommand;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentcolor.LoadAssessmentColorByIdPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreateAssessmentService implements CreateAssessmentUseCase {

    private final CreateAssessmentPort createAssessmentPort;
    private final LoadAssessmentColorByIdPort loadAssessmentColorByIdPort;

    @Override
    @Transactional
    public UUID createAssessment(CreateAssessmentCommand command) {
        CreateAssessmentCommand refinedCommand = refineProperties(command);
        return createAssessmentPort.persist(refinedCommand);
    }

    private CreateAssessmentCommand refineProperties(CreateAssessmentCommand createCommand) {
        String code = createCommand.generateSlugCodeByTitle();
        AssessmentColorDto color = loadColor(createCommand.getColor());

        return new CreateAssessmentCommand(
            code,
            createCommand.getTitle(),
            createCommand.getDescription(),
            createCommand.getCreationTime(),
            createCommand.getLastModificationDate(),
            createCommand.getAssessmentKitId(),
            color,
            createCommand.getSpaceId()
        );
    }

    private AssessmentColorDto loadColor(AssessmentColorDto color) {
        if (color.getId() == null) {
            return color;
        }
        AssessmentColorDto detailsDto = loadAssessmentColorByIdPort.loadById(color.getId());
        if (detailsDto == null)
            throw new ResourceNotFoundException("Color with ID " + color.getId() + " not found.");
        return detailsDto;
    }

}
