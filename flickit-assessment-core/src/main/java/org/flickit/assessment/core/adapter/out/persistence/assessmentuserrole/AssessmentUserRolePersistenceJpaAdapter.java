package org.flickit.assessment.core.adapter.out.persistence.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadAssessmentPrivilegedUsersPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.flickit.assessment.data.jpa.core.assessmentuserrole.AssessmentPrivilegedUserView;
import org.flickit.assessment.data.jpa.core.assessmentuserrole.AssessmentUserRoleJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentuserrole.AssessmentUserRoleJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.GRANT_ASSESSMENT_USER_ROLE_ROLE_ID_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.GRANT_ASSESSMENT_USER_ROLE_USER_ROLE_DUPLICATE;

@Component
@RequiredArgsConstructor
public class AssessmentUserRolePersistenceJpaAdapter implements
    LoadUserRoleForAssessmentPort,
    GrantUserAssessmentRolePort,
    LoadAssessmentPrivilegedUsersPort {

    private final AssessmentUserRoleJpaRepository repository;

    @Override
    public AssessmentUserRole load(UUID assessmentId, UUID userId) {
        return repository.findByAssessmentIdAndUserId(assessmentId, userId)
            .map(x -> AssessmentUserRole.valueOfById(x.getRoleId()))
            .orElse(null);
    }

    @Override
    public void persist(UUID assessmentId, UUID userId, Integer roleId) {
        var userRoleOnAssessment = repository.findByAssessmentIdAndUserId(assessmentId, userId);
        if (userRoleOnAssessment.isPresent())
            throw new ResourceAlreadyExistsException(GRANT_ASSESSMENT_USER_ROLE_USER_ROLE_DUPLICATE);

        if (!AssessmentUserRole.isValidId(roleId))
            throw new ResourceNotFoundException(GRANT_ASSESSMENT_USER_ROLE_ROLE_ID_NOT_FOUND);

        var entity = new AssessmentUserRoleJpaEntity(assessmentId, userId, roleId);
        repository.save(entity);
    }

    @Override
    public PaginatedResponse<AssessmentPrivilegedUser> loadAssessmentPrivilegedUsers(Param param) {
        Page<AssessmentPrivilegedUserView> pageResult = repository.findAssessmentPrivilegedUsers(param.assessmentId(),
            PageRequest.of(param.page(), param.size()));

        List<AssessmentPrivilegedUser> assessmentPrivilegedUsers = pageResult.getContent().stream()
            .map(e -> {
                AssessmentUserRole assessmentUserRole = AssessmentUserRole.valueOfById(e.getRoleId());

                return new AssessmentPrivilegedUser(e.getUserId(),
                    e.getEmail(),
                    e.getDisplayName(),
                    e.getBio(),
                    e.getPicturePath(),
                    e.getLinkedin(),
                    new AssessmentPrivilegedUser.Role(e.getRoleId(), assessmentUserRole.getTitle()));
            }).toList();

        return new PaginatedResponse<>(
            assessmentPrivilegedUsers,
            pageResult.getNumber(),
            pageResult.getSize(),
            AssessmentUserRoleJpaEntity.Fields.ROLE_ID,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }
}
