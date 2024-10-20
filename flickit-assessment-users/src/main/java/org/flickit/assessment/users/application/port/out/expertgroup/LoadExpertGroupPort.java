package org.flickit.assessment.users.application.port.out.expertgroup;

import org.flickit.assessment.users.application.domain.ExpertGroup;

public interface LoadExpertGroupPort {

    ExpertGroup loadExpertGroup(long id);
}
