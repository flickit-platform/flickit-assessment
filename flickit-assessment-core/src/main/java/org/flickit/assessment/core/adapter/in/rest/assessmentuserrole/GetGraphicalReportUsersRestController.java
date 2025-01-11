package org.flickit.assessment.core.adapter.in.rest.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GetGraphicalReportUsersUseCase;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GetGraphicalReportUsersUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetGraphicalReportUsersRestController {

    private final GetGraphicalReportUsersUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/users-with-report-access")
    public ResponseEntity<Result> getGraphicalReportUsers(@PathVariable("assessmentId") UUID assessmentId) {
        var currentUserId = userContext.getUser().id();
        var result = useCase.getGraphicalReportUsers(toParam(assessmentId, currentUserId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private static GetGraphicalReportUsersUseCase.Param toParam(UUID assessmentId, UUID currentUserId) {
        return new GetGraphicalReportUsersUseCase.Param(assessmentId, currentUserId);
    }
}
