package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.common.config.jwt.UserDetail;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentUseCase;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateAssessmentRestController {

    private final CreateAssessmentUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessments")
    public ResponseEntity<CreateAssessmentResponseDto> createAssessment(@RequestBody CreateAssessmentRequestDto request) {
        userContext.setUser(new UserDetail(UUID.fromString("00000000-0000-0000-0000-0000000003e8")));
        UUID currentUserId = userContext.getUser().id();
        CreateAssessmentResponseDto response = toResponseDto(useCase.createAssessment(toParam(request, currentUserId)));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private Param toParam(CreateAssessmentRequestDto requestDto, UUID currentUserId) {
        return new Param(
            requestDto.spaceId(),
            requestDto.title(),
            requestDto.assessmentKitId(),
            requestDto.colorId(),
            currentUserId
        );
    }

    private CreateAssessmentResponseDto toResponseDto(CreateAssessmentUseCase.Result result) {
        return new CreateAssessmentResponseDto(result.id());
    }
}
