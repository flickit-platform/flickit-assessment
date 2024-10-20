package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetExpertGroupKitListUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadExpertGroupKitListPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetExpertGroupKitListService implements GetExpertGroupKitListUseCase {

    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadExpertGroupKitListPort loadExpertGroupKitListPort;

    @Override
    public PaginatedResponse<Result> getExpertGroupKitList(Param param) {
        PaginatedResponse<AssessmentKit> kitPaginatedResponse;
        if (checkExpertGroupAccessPort.checkIsMember(param.getExpertGroupId(), param.getCurrentUserId())) {
            kitPaginatedResponse = loadExpertGroupKitListPort.loadExpertGroupKits(param.getExpertGroupId(),
                param.getCurrentUserId(),
                true,
                param.getPage(),
                param.getSize());
        } else {
            kitPaginatedResponse = loadExpertGroupKitListPort.loadExpertGroupKits(param.getExpertGroupId(),
                param.getCurrentUserId(),
                false,
                param.getPage(),
                param.getSize());
        }
        var items = kitPaginatedResponse.getItems().stream()
            .map(this::toResult)
            .toList();

        return new PaginatedResponse<>(
            items,
            kitPaginatedResponse.getPage(),
            kitPaginatedResponse.getSize(),
            kitPaginatedResponse.getSort(),
            kitPaginatedResponse.getOrder(),
            kitPaginatedResponse.getTotal()
        );
    }

    private Result toResult(AssessmentKit kit) {
        return new Result(kit.getId(),
            kit.getTitle(),
            kit.isPublished(),
            kit.isPrivate(),
            kit.getLastModificationTime(),
            kit.getDraftVersionId());
    }
}
