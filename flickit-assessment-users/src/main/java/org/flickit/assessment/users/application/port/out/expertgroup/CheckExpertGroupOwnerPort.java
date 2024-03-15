package org.flickit.assessment.users.application.port.out.expertgroup;

import java.util.UUID;

public interface CheckExpertGroupOwnerPort {

    boolean checkIsOwner(long expertGroupId, UUID userId);
}
