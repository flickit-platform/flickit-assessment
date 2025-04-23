package org.flickit.assessment.core.application.port.out.maturitylevel;

import org.flickit.assessment.core.application.domain.MaturityLevel;

import java.util.UUID;

public interface LoadMaturityLevelPort {

    MaturityLevel load(long id, UUID assessmentId);
}
