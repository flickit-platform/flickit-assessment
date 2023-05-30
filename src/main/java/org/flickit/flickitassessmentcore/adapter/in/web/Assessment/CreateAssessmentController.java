package org.flickit.flickitassessmentcore.adapter.in.web.Assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.Assessment.CreateAssessmentCommand;
import org.flickit.flickitassessmentcore.application.port.in.Assessment.CreateAssessmentUseCase;
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
@RequestMapping("/assessments")
public class CreateAssessmentController {
    private final CreateAssessmentUseCase createAssessmentUseCase;
    private final CreateAssessmentWebModelMapper mapper;

    @PostMapping
    public ResponseEntity<UUID> createAssessment(@RequestBody CreateAssessmentWebModel model) {
        CreateAssessmentCommand createAssessmentCommand = mapper.mapWebModelToCommand(model);
        UUID uuid = createAssessmentUseCase.createAssessment(createAssessmentCommand);
        return new ResponseEntity<>(uuid, HttpStatus.CREATED);
    }
}
