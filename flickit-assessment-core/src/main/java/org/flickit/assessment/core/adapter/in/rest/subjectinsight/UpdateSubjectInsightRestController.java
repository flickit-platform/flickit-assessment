package org.flickit.assessment.core.adapter.in.rest.subjectinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.subjectinsight.UpdateSubjectInsightUseCase;
import org.flickit.assessment.core.application.port.in.subjectinsight.UpdateSubjectInsightUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateSubjectInsightRestController {

    private final UpdateSubjectInsightUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessments/{assessmentId}/insights/subjects/{subjectId}")
    public ResponseEntity<Void> updateSubjectInsight(@PathVariable("assessmentId") UUID assessmentId,
                                                     @PathVariable("subjectId") Long subjectId,
                                                     @RequestBody UpdateSubjectInsightRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateSubjectInsight(toParam(assessmentId, subjectId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, Long subjectId, UpdateSubjectInsightRequestDto requestDto, UUID currentUserId) {
        return new Param(assessmentId, subjectId, requestDto.insight(), currentUserId);
    }
}
