package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GetAssessmentUserRolesUseCase;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static java.util.Comparator.comparing;

@Service
public class GetAssessmentUserRolesService implements GetAssessmentUserRolesUseCase {

    @Override
    public List<AssessmentUserRoleItem> getAssessmentUserRoles() {
        return Arrays.stream(AssessmentUserRole.values())
            .sorted(comparing(AssessmentUserRole::getIndex))
            .map(cl -> new GetAssessmentUserRolesUseCase.AssessmentUserRoleItem(cl.getId(), cl.getTitle(), cl.getDescription()))
            .toList();
    }
}
