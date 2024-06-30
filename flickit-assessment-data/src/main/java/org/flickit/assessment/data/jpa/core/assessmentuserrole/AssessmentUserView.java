package org.flickit.assessment.data.jpa.core.assessmentuserrole;

import java.util.UUID;

public interface AssessmentUserView {

    UUID getUserId();

    String getEmail();

    String getDisplayName();

    String getPicturePath();

    int getRoleId();

    String getTitle();

    boolean getEditable();
}
