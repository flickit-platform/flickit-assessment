package org.flickit.flickitassessmentcore.application.service.AssessmentProject;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.AssessmentProject.AssessmentColorDto;
import org.flickit.flickitassessmentcore.application.port.in.AssessmentProject.CreateAssessmentProjectCommand;
import org.flickit.flickitassessmentcore.application.port.in.AssessmentProject.CreateAssessmentProjectUseCase;
import org.flickit.flickitassessmentcore.application.port.out.AssessmentColor.LoadAssessmentColorByIdPort;
import org.flickit.flickitassessmentcore.application.port.out.AssessmentProject.CreateAssessmentProjectPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreateAssessmentProjectService implements CreateAssessmentProjectUseCase {

    private final CreateAssessmentProjectPort createAssessmentProjectPort;
    private final LoadAssessmentColorByIdPort loadAssessmentColorByIdPort;

    @Override
    public UUID createAssessmentProject(CreateAssessmentProjectCommand createAssessmentProjectCommand) {
        CreateAssessmentProjectCommand refinedCommand = refineProperties(createAssessmentProjectCommand);
        return createAssessmentProjectPort.persist(refinedCommand);
    }

    private CreateAssessmentProjectCommand refineProperties(CreateAssessmentProjectCommand createCommand) {
        String code = createCommand.generateSlugCodeByTitle();
        AssessmentColorDto color = loadColor(createCommand.getColor());

        return new CreateAssessmentProjectCommand(
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
            throw new AssessmentColorNotFoundException("Color with ID " + color.getId() + " not found.");
        return detailsDto;
    }

}
