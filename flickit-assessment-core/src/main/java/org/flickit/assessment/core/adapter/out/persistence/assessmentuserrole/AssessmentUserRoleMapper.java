package org.flickit.assessment.core.adapter.out.persistence.assessmentuserrole;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.domain.AssessmentUserRoleItem;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadAssessmentUsersPort;
import org.flickit.assessment.data.jpa.core.assessmentuserrole.AssessmentUserRoleJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentuserrole.AssessmentUserView;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentUserRoleMapper {

    static AssessmentUserRoleJpaEntity mapToJpEntity(AssessmentUserRoleItem item){
        return new AssessmentUserRoleJpaEntity(item.getAssessmentId(),
            item.getUserId(),
            item.getRole().getId(),
            item.getCreatedBy(),
            item.getCreationTime());
    }

    public static AssessmentUserRoleItem mapToRoleItem(AssessmentUserRoleJpaEntity entity) {
        return new AssessmentUserRoleItem(entity.getAssessmentId(),
            entity.getUserId(),
            AssessmentUserRole.valueOfById(entity.getRoleId()),
            entity.getCreatedBy(),
            entity.getCreationTime());
    }

    public static LoadAssessmentUsersPort.ReportUser mapToPortResult(AssessmentUserView view) {
        return new LoadAssessmentUsersPort.ReportUser(view.getUserId(),
            view.getDisplayName(),
            view.getEmail(),
            view.getPicturePath(),
            view.getCreatedBy(),
            AssessmentUserRole.valueOfById(view.getRoleId()));
    }
}
