package org.flickit.assessment.kit.application.port.out.maturitylevel;

import org.flickit.assessment.kit.application.domain.MaturityLevel;

import java.util.Optional;

public interface LoadMaturityLevelPort {

    Optional<MaturityLevel> load(Long id);
}
