package org.flickit.assessment.data.jpa.kit.expertgroup;

import java.util.UUID;

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
