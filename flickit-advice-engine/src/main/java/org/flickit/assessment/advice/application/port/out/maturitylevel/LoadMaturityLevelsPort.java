package org.flickit.assessment.advice.application.port.out.maturitylevel;

import org.flickit.assessment.advice.application.domain.MaturityLevel;

import java.util.List;
import java.util.UUID;

public interface LoadMaturityLevelsPort {

    List<MaturityLevel> loadAll(UUID assessmentId);
}
