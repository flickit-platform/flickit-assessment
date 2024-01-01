package org.flickit.assessment.data.jpa.kit.expertgroup;

public interface ExpertGroupWithAssessmentKitCountView {
    Long getId();

    String getName();

    String getPicture();

    String getBio();

    Integer getPublishedKitsCount();
    Boolean getEditable();
}
