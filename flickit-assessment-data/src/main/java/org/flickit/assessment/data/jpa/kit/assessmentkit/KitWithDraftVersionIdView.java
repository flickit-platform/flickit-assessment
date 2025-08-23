package org.flickit.assessment.data.jpa.kit.assessmentkit;

public interface KitWithDraftVersionIdView {

    AssessmentKitJpaEntity getKit();

    Long getDraftVersionId();
}
