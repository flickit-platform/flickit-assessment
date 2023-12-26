package org.flickit.assessment.kit.application.service.expertgroup;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupListUseCase;
import org.springframework.stereotype.Service;

@Service
public class GetExpertUserListService implements GetExpertGroupListUseCase {
    @Override
    public PaginatedResponse<ExpertGroupListItem> getExpertGroupList(Param param) {
        return null;
    }
}
