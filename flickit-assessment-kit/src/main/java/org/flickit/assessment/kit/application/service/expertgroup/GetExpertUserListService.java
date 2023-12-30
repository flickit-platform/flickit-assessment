package org.flickit.assessment.kit.application.service.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupListUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupListPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetExpertUserListService implements GetExpertGroupListUseCase {

    private final LoadExpertGroupListPort loadExpertGroupListPort;
    @Override
    public PaginatedResponse<ExpertGroupListItem> getExpertGroupList(Param param) {
        return loadExpertGroupListPort.loadExpertGroupList(toParam(param.getPage(), param.getSize()));
    }

    private LoadExpertGroupListPort.Param toParam(int page, int size){
        return  new LoadExpertGroupListPort.Param(page, size);
    }
}
