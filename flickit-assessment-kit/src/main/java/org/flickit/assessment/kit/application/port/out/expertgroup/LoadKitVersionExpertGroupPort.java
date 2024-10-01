package org.flickit.assessment.kit.application.port.out.expertgroup;

import org.flickit.assessment.kit.application.domain.ExpertGroup;

public interface LoadKitVersionExpertGroupPort {

    ExpertGroup loadKitVersionExpertGroup(long kitVersionId);
}
