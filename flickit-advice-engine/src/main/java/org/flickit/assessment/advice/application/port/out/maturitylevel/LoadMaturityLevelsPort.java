package org.flickit.assessment.advice.application.port.out.maturitylevel;

import org.flickit.assessment.advice.application.domain.MaturityLevel;

import java.util.List;

public interface LoadMaturityLevelsPort {

    List<MaturityLevel> loadAll(long kitVersionId);
}
