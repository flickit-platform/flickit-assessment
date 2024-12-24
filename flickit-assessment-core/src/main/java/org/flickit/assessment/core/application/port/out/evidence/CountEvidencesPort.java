package org.flickit.assessment.core.application.port.out.evidence;

import java.util.UUID;

public interface CountEvidencesPort {

    int countQuestionsHavingEvidence(UUID assessmentId);

    int countUnresolvedComments(UUID assessmentId);
}
