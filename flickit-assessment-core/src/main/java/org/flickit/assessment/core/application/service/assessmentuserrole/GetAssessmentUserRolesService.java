package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.core.application.port.in.assessmentuserrole.GetAssessmentUserRolesUseCase;
import org.flickit.assessment.core.common.AssessmentUserRole;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GetAssessmentUserRolesService implements GetAssessmentUserRolesUseCase {

    @Override
    public List<AssessmentUserRoleItem> getAssessmentUserRoles() {
        return Arrays.stream(AssessmentUserRole.values())
            .map(cl -> new GetAssessmentUserRolesUseCase.AssessmentUserRoleItem(cl.getId(), cl.getTitle()))
            .toList();
    }
}
