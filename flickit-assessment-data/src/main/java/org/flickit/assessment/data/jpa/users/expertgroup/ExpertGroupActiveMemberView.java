package org.flickit.assessment.data.jpa.users.expertgroup;

import java.util.UUID;

public interface ExpertGroupActiveMemberView {

    UUID getId();

    String getEmail();

    String getDisplayName();
}
