package org.flickit.assessment.core.application.service.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GetAssessmentPrivilegedUsersUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentPrivilegedUsersService implements GetAssessmentPrivilegedUsersUseCase {

    @Override
    public PaginatedResponse<AssessmentPrivilegedUser> getAssessmentPrivilegedUsers(Param param) {
        return null;
    }
}
