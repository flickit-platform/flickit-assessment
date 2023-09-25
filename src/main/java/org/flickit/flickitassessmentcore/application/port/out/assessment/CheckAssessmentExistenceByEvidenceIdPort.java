package org.flickit.flickitassessmentcore.application.port.out.assessment;

import java.util.UUID;

public interface CheckAssessmentExistenceByEvidenceIdPort {

    boolean isAssessmentExistsByEvidenceId(UUID evidenceId);
}
