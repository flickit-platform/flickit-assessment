package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessment.UpdateAssessmentUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateAssessmentRestController {

    private final UpdateAssessmentUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/assessments/{id}")
    public ResponseEntity<UpdateAssessmentResponseDto> updateAssessment(@PathVariable("id") UUID id,
                                                                        @RequestBody UpdateAssessmentRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        UpdateAssessmentResponseDto responseDto = toResponseDto(useCase.updateAssessment(toParam(id, requestDto, currentUserId)));
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    private UpdateAssessmentUseCase.Param toParam(UUID id, UpdateAssessmentRequestDto request, UUID currentUserId) {
        return new UpdateAssessmentUseCase.Param(
            id,
            request.title(),
            currentUserId
        );
    }

    private UpdateAssessmentResponseDto toResponseDto(UpdateAssessmentUseCase.Result result) {
        return new UpdateAssessmentResponseDto(result.id());
    }
}
