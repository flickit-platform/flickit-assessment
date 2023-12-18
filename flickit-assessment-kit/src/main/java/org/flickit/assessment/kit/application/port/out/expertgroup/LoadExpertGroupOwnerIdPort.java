package org.flickit.assessment.kit.application.port.out.expertgroup;

import java.util.UUID;

public interface LoadExpertGroupOwnerIdPort {

    UUID loadByKitId(Long kitId);
}
