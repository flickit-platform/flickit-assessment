package org.flickit.assessment.core.adapter.in.rest.subjectinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.subjectinsight.CreateSubjectInsightUseCase;
import org.flickit.assessment.core.application.port.in.subjectinsight.CreateSubjectInsightUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateSubjectInsightRestController {

    private final CreateSubjectInsightUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessments/{assessmentId}/insights/subjects/{subjectId}")
    public ResponseEntity<Void> createSubjectInsightOld(@PathVariable("assessmentId") UUID assessmentId,
                                                        @PathVariable("subjectId") Long subjectId,
                                                        @RequestBody CreateSubjectInsightRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.createSubjectInsight(toParam(assessmentId, subjectId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/assessments/{assessmentId}/subjects/{subjectId}/insight")
    public ResponseEntity<Void> createSubjectInsight(@PathVariable("assessmentId") UUID assessmentId,
                                                     @PathVariable("subjectId") Long subjectId,
                                                     @RequestBody CreateSubjectInsightRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.createSubjectInsight(toParam(assessmentId, subjectId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private Param toParam(UUID assessmentId, Long subjectId, CreateSubjectInsightRequestDto requestDto, UUID currentUserId) {
        return new Param(assessmentId, subjectId, requestDto.insight(), currentUserId);
    }
}
