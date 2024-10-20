package org.flickit.assessment.data.jpa.kit.assessmentkit;

import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaEntity;

public interface KitWithExpertGroupView {

    AssessmentKitJpaEntity getKit();

    ExpertGroupJpaEntity getExpertGroup();
}
