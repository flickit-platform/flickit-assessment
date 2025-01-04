package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessment.GrantAccessToReportUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GrantAccessToReportRestController {

    private final GrantAccessToReportUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessments/{assessmentId}/grant-report-access")
    public ResponseEntity<Void> grantAccessToReport(@PathVariable("assessmentId") UUID assessmentId,
                                                    @RequestBody GrantAccessToReportRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.grantAccessToReport(toParam(requestDto.email(), assessmentId, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private static GrantAccessToReportUseCase.Param toParam(String email, UUID assessmentId, UUID currentUserId) {
        return new GrantAccessToReportUseCase.Param(email, assessmentId, currentUserId);
    }
}
