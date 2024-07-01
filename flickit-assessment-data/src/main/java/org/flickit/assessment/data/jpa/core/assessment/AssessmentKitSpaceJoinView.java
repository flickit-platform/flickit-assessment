package org.flickit.assessment.data.jpa.core.assessment;

import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;

public interface AssessmentKitSpaceJoinView {

    AssessmentJpaEntity getAssessment();

    AssessmentKitJpaEntity getKit();

    SpaceJpaEntity getSpace();
}
