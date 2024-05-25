package org.flickit.assessment.core.adapter.in.rest.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GetAssessmentUserRolesUseCase;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GetAssessmentUserRolesUseCase.AssessmentUserRoleItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GetAssessmentUserRolesRestController {

    private final GetAssessmentUserRolesUseCase useCase;

    @GetMapping("/assessment-user-roles")
    public ResponseEntity<GetAssessmentUserRolesResponseDto> getAssessmentUserRoles() {
        var response = useCase.getAssessmentUserRoles();
        GetAssessmentUserRolesResponseDto responseDto = toResponseDto(response);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    private GetAssessmentUserRolesResponseDto toResponseDto(List<AssessmentUserRoleItem> response) {
        return new GetAssessmentUserRolesResponseDto(response);
    }
}
