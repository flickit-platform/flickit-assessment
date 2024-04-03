package org.flickit.assessment.users.application.port.out.expertgroup;

import java.util.UUID;

public interface LoadExpertGroupOwnerPort {

    UUID loadOwnerId(Long expertGroupId);
}
