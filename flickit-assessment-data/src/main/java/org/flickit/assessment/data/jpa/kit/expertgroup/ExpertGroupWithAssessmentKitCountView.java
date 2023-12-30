package org.flickit.assessment.data.jpa.kit.expertgroup;

import jakarta.persistence.Column;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;

import java.util.UUID;

// 1) To incorporate a new field named 'publishedKitsCount' into the JPA Entity, I have created the following interface:

public interface ExpertGroupWithAssessmentKitCountView {
    Long getId();

    String getName();

    String getAbout();

    String getPicture();

    String getWebsite();

    String getBio();

    UUID getOwnerId();
    Long getPublishedKitsCount();
}
