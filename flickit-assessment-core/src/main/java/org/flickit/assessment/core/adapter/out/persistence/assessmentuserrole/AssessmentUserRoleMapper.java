package org.flickit.assessment.core.adapter.out.persistence.assessmentuserrole;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.domain.AssessmentUserRoleItem;
import org.flickit.assessment.data.jpa.core.assessmentuserrole.AssessmentUserRoleJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentUserRoleMapper {

    static AssessmentUserRoleJpaEntity mapToJpEntity(AssessmentUserRoleItem item){
        return new AssessmentUserRoleJpaEntity(item.getAssessmentId(),
            item.getUserId(),
            item.getRole().getId(),
            item.getCreatedBy(),
            item.getCreationTime());
    }

    public static AssessmentUserRoleItem mapToRoleItem(AssessmentUserRoleJpaEntity jpaEntity) {
        return new AssessmentUserRoleItem(jpaEntity.getAssessmentId(),
            jpaEntity.getUserId(),
            AssessmentUserRole.valueOfById(jpaEntity.getRoleId()),
            jpaEntity.getCreatedBy(),
            jpaEntity.getCreationTime());
    }
}
