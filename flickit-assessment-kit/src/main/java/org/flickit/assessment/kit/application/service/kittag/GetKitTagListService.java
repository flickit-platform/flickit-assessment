package org.flickit.assessment.kit.application.service.kittag;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.KitTag;
import org.flickit.assessment.kit.application.port.in.kittag.GetKitTagListUseCase;
import org.flickit.assessment.kit.application.port.out.kittag.LoadKitTagListPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitTagListService implements GetKitTagListUseCase {

    private final LoadKitTagListPort loadKitTagListPort;

    @Override
    public PaginatedResponse<KitTag> getKitTagList(Param param) {
        return loadKitTagListPort.loadAll(param.getPage(), param.getSize());
    }
}
