package org.flickit.assessment.data.jpa.core.evidence;

import java.time.LocalDateTime;
import java.util.UUID;

public interface EvidenceWithAttachmentsCountView {

    UUID getId();

    String getDescription();

    Integer getType();

    UUID getCreatedBy();

    LocalDateTime getLastModificationTime();

    Integer getAttachmentsCount();
}
