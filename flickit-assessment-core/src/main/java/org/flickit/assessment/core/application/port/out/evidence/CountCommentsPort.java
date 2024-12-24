package org.flickit.assessment.core.application.port.out.evidence;

import java.util.UUID;

public interface CountCommentsPort {

    int countUnResolvedResolvedComments(UUID assessmentResultId);
}
