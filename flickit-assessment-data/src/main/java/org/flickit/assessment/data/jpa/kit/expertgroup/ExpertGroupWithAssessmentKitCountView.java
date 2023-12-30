package org.flickit.assessment.data.jpa.kit.expertgroup;

import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;

// 1) To incorporate a new field named 'publishedKitsCount' into the JPA Entity, I have created the following interface:

public interface ExpertGroupWithAssessmentKitCountView {
    ExpertGroupJpaEntity getExpertGroup();

    Long getPublishedKitsCount();
}
