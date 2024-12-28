package org.flickit.assessment.core.adapter.in.rest.subjectinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.subjectinsight.ApproveSubjectInsightUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ApproveSubjectInsightRestController {

    private final ApproveSubjectInsightUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/assessments/{assessmentId}/insights/subjects/{subjectId}/approve")
    public ResponseEntity<Void> approveSubjectInsight(@PathVariable("assessmentId")UUID assessmentId,
                                                      @PathVariable("subjectId") Long subjectId) {

        UUID currentUserId = userContext.getUser().id();
        useCase.approveSubjectInsight(toParam(assessmentId, subjectId, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private static ApproveSubjectInsightUseCase.Param toParam(UUID assessmentId, Long subjectId, UUID currentUserId) {
        return new ApproveSubjectInsightUseCase.Param(assessmentId, subjectId, currentUserId);
    }
}
