package org.flickit.assessment.kit.application.port.out.maturitylevel;

import org.flickit.assessment.kit.application.domain.MaturityLevel;

public interface LoadMaturityLevelByTitlePort {

    MaturityLevel loadByTitle(String title, Long kitId);
}
