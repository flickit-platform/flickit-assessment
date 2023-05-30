package org.flickit.flickitassessmentcore.adapter.in.web.AssessmentProject;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.AssessmentProject.CreateAssessmentProjectCommand;
import org.flickit.flickitassessmentcore.application.port.in.AssessmentProject.CreateAssessmentProjectUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<UUID> createAssessmentProject(@RequestBody CreateAssessmentProjectWebModel model) {
        CreateAssessmentProjectCommand createAssessmentProjectCommand = mapper.mapWebModelToCommand(model);
        UUID uuid = createAssessmentProjectUseCase.createAssessmentProject(createAssessmentProjectCommand);
        return new ResponseEntity<>(uuid, HttpStatus.CREATED);
    }
}
