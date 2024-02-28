package org.flickit.assessment.core.adapter.in.rest.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.subject.GetSubjectProgressUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetSubjectProgressRestController {

    private final GetSubjectProgressUseCase useCase;

    @GetMapping("/assessments/{assessmentId}/subjects/{subjectId}/progress")
    public ResponseEntity<GetSubjectProgressResponseDto> getSubjectProgress(
        @PathVariable("assessmentId") UUID assessmentId,
        @PathVariable("subjectId") Long subjectId) {
        var response = toResponse(useCase.getSubjectProgress(toParam(assessmentId, subjectId)));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private GetSubjectProgressUseCase.Param toParam(UUID assessmentId, Long subjectId) {
        return new GetSubjectProgressUseCase.Param(assessmentId, subjectId);
    }

    private GetSubjectProgressResponseDto toResponse(GetSubjectProgressUseCase.Result result) {
        return new GetSubjectProgressResponseDto(result.id(), result.title(), result.answerCount(), result.questionCount());
    }
}
