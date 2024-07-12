package org.flickit.assessment.data.jpa.core.evidence;

import java.util.UUID;

public interface EvidenceDescriptionAttachmentsCountView {

    UUID getId();

    String getDescription();

    int getAttachmentsCount();
}
