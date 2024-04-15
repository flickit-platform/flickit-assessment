package org.flickit.assessment.kit.application.port.out.maturitylevel;


import org.flickit.assessment.kit.application.domain.MaturityLevel;

import java.util.List;

public interface LoadMaturityLevelsPort {

    List<MaturityLevel> loadByKitId(Long kitId);
}
