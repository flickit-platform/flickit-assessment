package org.flickit.assessment.core.adapter.in.rest.assessmentreport;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentreport.UpdateAssessmentReportVisibilityUseCase;
import org.flickit.assessment.core.application.port.in.assessmentreport.UpdateAssessmentReportVisibilityUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateAssessmentReportRestController {

    private final UpdateAssessmentReportVisibilityUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/assessments/{assessmentId}/report-visibility-status")
    public ResponseEntity<Result> updateAssessmentReportVisibility(@PathVariable UUID assessmentId,
                                                                   @RequestBody UpdateAssessmentReportVisibilityRequestDto request) {
        UUID currentUserId = userContext.getUser().id();
        var result = useCase.updateReportVisibility(toParam(assessmentId, request.visibility(), currentUserId));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, String visibility, UUID currentUserId) {
        return new Param(assessmentId, visibility, currentUserId);
    }
}
