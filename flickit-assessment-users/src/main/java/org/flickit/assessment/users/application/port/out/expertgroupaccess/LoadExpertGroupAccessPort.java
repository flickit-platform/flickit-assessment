package org.flickit.assessment.users.application.port.out.expertgroupaccess;

import java.time.LocalDateTime;
import java.util.UUID;

public interface LoadExpertGroupAccessPort {

    LocalDateTime loadExpirationDate(long expertGroupId, UUID token, UUID userId);
}
