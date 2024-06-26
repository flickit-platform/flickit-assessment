package org.flickit.assessment.data.jpa.core.assessment;

import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;

public interface ComparableAssessmentListItemView {

    AssessmentJpaEntity getAssessment();

    AssessmentResultJpaEntity getAssessmentResult();

    AssessmentKitJpaEntity getAssessmentKit();

    SpaceJpaEntity getSpace();
}
