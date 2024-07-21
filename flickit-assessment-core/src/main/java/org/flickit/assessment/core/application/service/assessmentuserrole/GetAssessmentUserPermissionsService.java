package org.flickit.assessment.core.application.service.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GetAssessmentUserPermissionsUseCase;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static java.util.stream.Collectors.toMap;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentUserPermissionsService implements GetAssessmentUserPermissionsUseCase {

    private final LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;

    @Override
    public Result getAssessmentUserPermissions(Param param) {
        var userRole = loadUserRoleForAssessmentPort.load(param.getAssessmentId(), param.getUserId());

        return userRole.map(assessmentUserRole -> new Result(Arrays.stream(AssessmentPermission.values())
                .collect(toMap(AssessmentPermission::getCode, assessmentUserRole::hasAccess))))
            .orElseGet(() -> new Result(Arrays.stream(AssessmentPermission.values())
                .collect(toMap(AssessmentPermission::getCode, e -> false))));
    }
}
