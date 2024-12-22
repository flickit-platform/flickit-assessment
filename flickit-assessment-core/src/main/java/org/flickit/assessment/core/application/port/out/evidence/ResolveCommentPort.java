package org.flickit.assessment.core.application.port.out.evidence;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ResolveCommentPort {

    void resolveComment(UUID commentId, UUID lastModifiedBy, LocalDateTime lastModificationTime);
}
