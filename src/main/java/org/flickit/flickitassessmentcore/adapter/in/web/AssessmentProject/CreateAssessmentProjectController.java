package org.flickit.flickitassessmentcore.adapter.in.web.AssessmentProject;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.AssessmentProject.CreateAssessmentProjectCommand;
import org.flickit.flickitassessmentcore.application.port.in.AssessmentProject.CreateAssessmentProjectUseCase;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@RestController
@RequestMapping("/assessment/projects")
public class CreateAssessmentProjectController {
    private final CreateAssessmentProjectUseCase createAssessmentProjectUseCase;
    private final CreateAssessmentProjectWebModelMapper mapper;

    @PostMapping
    public UUID createAssessmentProject(CreateAssessmentProjectWebModel model) {
        CreateAssessmentProjectCommand createAssessmentProjectCommand = mapper.mapWebModelToCommand(model);
        return createAssessmentProjectUseCase.createAssessmentProject(createAssessmentProjectCommand);
    }
}
