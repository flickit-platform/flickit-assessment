package org.flickit.assessment.core.adapter.out.persistence.assessmentuserrole;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.AssessmentUserRoleItem;
import org.flickit.assessment.data.jpa.core.assessmentuserrole.AssessmentUserRoleJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentUseRoleMapper {

    static AssessmentUserRoleJpaEntity mapToJpEntity(AssessmentUserRoleItem item){
        return new AssessmentUserRoleJpaEntity(item.getAssessmentId(),
            item.getUserId(),
            item.getRoleId());
    }
}
