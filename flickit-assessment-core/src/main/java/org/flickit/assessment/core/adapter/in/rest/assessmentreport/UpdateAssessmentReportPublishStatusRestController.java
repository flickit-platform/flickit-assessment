package org.flickit.assessment.core.adapter.in.rest.assessmentreport;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentreport.UpdateAssessmentReportPublishStatusUseCase;
import org.flickit.assessment.core.application.port.in.assessmentreport.UpdateAssessmentReportPublishStatusUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateAssessmentReportPublishStatusRestController {

    private final UpdateAssessmentReportPublishStatusUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/assessments/{assessmentId}/update-publish-status")
    public ResponseEntity<Void> updateReportPublishStatus(@PathVariable UUID assessmentId,
                                                          @RequestBody UpdateAssessmentReportPublishStatusRequestDto request) {
        var currentUserId = userContext.getUser().id();
        useCase.updateReportPublishStatus(toParam(assessmentId, request, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, UpdateAssessmentReportPublishStatusRequestDto request, UUID currentUserId) {
        return new Param(assessmentId, request.published(), currentUserId);
    }
}
