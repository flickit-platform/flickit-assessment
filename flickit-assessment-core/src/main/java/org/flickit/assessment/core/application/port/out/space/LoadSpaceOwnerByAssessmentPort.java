package org.flickit.assessment.core.application.port.out.space;

import java.util.UUID;

public interface LoadSpaceOwnerByAssessmentPort {

    UUID loadOwnerIdByAssessmentId(UUID assessmentId);
}
