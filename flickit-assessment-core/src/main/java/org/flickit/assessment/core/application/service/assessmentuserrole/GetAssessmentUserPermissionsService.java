package org.flickit.assessment.core.application.service.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GetAssessmentUserPermissionsUseCase;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentUserPermissionsService implements GetAssessmentUserPermissionsUseCase {

    private final LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;

    @Override
    public Map<String, Boolean> getAssessmentUserPermissions(Param param) {
        var optionalAssessmentUserRole = loadUserRoleForAssessmentPort.load(param.getAssessmentId(), param.getUserId());

        if (optionalAssessmentUserRole.isPresent()) {
            AssessmentUserRole assessmentUserRole = optionalAssessmentUserRole.get();
            return Arrays.stream(AssessmentPermission.values())
                .collect(Collectors.toMap(AssessmentPermission::getCode, assessmentUserRole::hasAccess));
        } else
            return Arrays.stream(AssessmentPermission.values())
                .collect(Collectors.toMap(AssessmentPermission::getCode, e -> false));
    }
}
