package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.flickit.assessment.kit.application.domain.ExpertGroup;

public interface LoadKitExpertGroupPort {

    ExpertGroup loadKitExpertGroup(Long kitId);
}
