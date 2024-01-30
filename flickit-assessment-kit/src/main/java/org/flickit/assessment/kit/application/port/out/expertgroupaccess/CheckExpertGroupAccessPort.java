package org.flickit.assessment.kit.application.port.out.expertgroupaccess;

import java.util.UUID;

public interface CheckExpertGroupAccessPort {

    boolean checkIsMember(long expertGroupId, UUID userId);
}
