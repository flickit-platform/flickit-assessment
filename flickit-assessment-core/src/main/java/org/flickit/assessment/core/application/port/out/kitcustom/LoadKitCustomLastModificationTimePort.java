package org.flickit.assessment.core.application.port.out.kitcustom;

import java.time.LocalDateTime;

public interface LoadKitCustomLastModificationTimePort {

    LocalDateTime loadLastModificationTime(long kitCustomId);
}
