package org.flickit.assessment.data.jpa.core.assessmentuserrole;

import java.util.UUID;

public interface AssessmentPrivilegedUserView {

    UUID getUserId();

    String getEmail();

    String getDisplayName();

    String getBio();

    String getPicturePath();

    String getLinkedin();

    int getRoleId();

    String getTitle();
}
