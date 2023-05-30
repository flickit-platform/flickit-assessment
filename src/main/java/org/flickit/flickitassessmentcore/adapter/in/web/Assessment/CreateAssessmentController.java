package org.flickit.flickitassessmentcore.adapter.in.web.Assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.Assessment.CreateAssessmentCommand;
import org.flickit.flickitassessmentcore.application.port.in.Assessment.CreateAssessmentUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@RestController
@RequestMapping("{spaceId}/assessments")
public class CreateAssessmentController {
    private final CreateAssessmentUseCase createAssessmentUseCase;
    private final CreateAssessmentWebModelMapper mapper;

    @PostMapping
    public ResponseEntity<UUID> createAssessment(@RequestBody CreateAssessmentWebModel model, @PathVariable("spaceId") Long spaceId) {
        CreateAssessmentCommand createAssessmentCommand = mapper.mapWebModelToCommand(model, spaceId);
        UUID uuid = createAssessmentUseCase.createAssessment(createAssessmentCommand);
        return new ResponseEntity<>(uuid, HttpStatus.CREATED);
    }
}
