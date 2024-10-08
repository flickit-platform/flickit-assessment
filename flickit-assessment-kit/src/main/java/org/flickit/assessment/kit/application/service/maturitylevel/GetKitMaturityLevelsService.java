package org.flickit.assessment.kit.application.service.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.port.in.maturitylevel.GetKitMaturityLevelsUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitMaturityLevelsService implements GetKitMaturityLevelsUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Override
    public PaginatedResponse<MaturityLevelListItem> getKitMaturityLevels(Param param) {
        var kit = loadKitVersionPort.load(param.getKitVersionId()).getKit();
        if (!checkExpertGroupAccessPort.checkIsMember(kit.getExpertGroupId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        var paginatedResponse = loadMaturityLevelsPort.loadByKitVersionId(param.getKitVersionId(), param.getSize(), param.getPage());
        var items = paginatedResponse.getItems().stream().map(this::toMaturityLevel).toList();
        return new PaginatedResponse<>(items,
            paginatedResponse.getPage(),
            paginatedResponse.getSize(),
            paginatedResponse.getSort(),
            paginatedResponse.getOrder(),
            paginatedResponse.getTotal());
    }

    private MaturityLevelListItem toMaturityLevel(MaturityLevel maturityLevel) {
        return new MaturityLevelListItem(maturityLevel.getId(),
            maturityLevel.getIndex(),
            maturityLevel.getTitle(),
            maturityLevel.getDescription(),
            maturityLevel.getValue()
        );
    }
}
