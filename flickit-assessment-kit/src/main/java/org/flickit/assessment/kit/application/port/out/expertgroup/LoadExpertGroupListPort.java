package org.flickit.assessment.kit.application.port.out.expertgroup;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupListUseCase.ExpertGroupListItem;

import java.util.UUID;

public interface LoadExpertGroupListPort {

    PaginatedResponse<ExpertGroupListItem> loadExpertGroupList(Param param);

    record Param(int page, int size, UUID currentUserID) {
    }
}
