package org.flickit.assessment.kit.application.port.out.maturitylevel;

import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.domain.MaturityLevelOrder;

import java.util.List;
import java.util.UUID;

public interface UpdateMaturityLevelPort {

    void update(List<MaturityLevel> maturityLevels, Long kitVersionId, UUID lastModifiedBy);

    void updateOrders(List<MaturityLevelOrder> maturityLevelOrders, Long kitVersionId, UUID lastModifiedBy);
}
