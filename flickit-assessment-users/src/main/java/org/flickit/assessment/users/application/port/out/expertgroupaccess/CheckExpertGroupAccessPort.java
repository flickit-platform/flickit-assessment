package org.flickit.assessment.users.application.port.out.expertgroupaccess;

import java.util.UUID;

public interface CheckExpertGroupAccessPort {

    boolean checkIsMember(long expertGroupId, UUID userId);
}
