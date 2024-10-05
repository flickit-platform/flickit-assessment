package org.flickit.assessment.kit.application.service.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.MaturityLevelOrder;
import org.flickit.assessment.kit.application.port.in.maturitylevel.UpdateMaturityLevelOrdersUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.UpdateMaturityLevelPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateMaturityLevelOrdersService implements UpdateMaturityLevelOrdersUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final UpdateMaturityLevelPort updateMaturityLevelPort;

    @Override
    public void changeOrders(Param param) {
        var kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());

        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var maturityLevelOrders = param.getOrders().stream().map(this::toMaturityLevelOrderParam).toList();
        updateMaturityLevelPort.updateOrders(maturityLevelOrders, param.getKitVersionId(), param.getCurrentUserId());
    }

    private MaturityLevelOrder toMaturityLevelOrderParam(MaturityLevelParam param) {
        return new MaturityLevelOrder(param.getId(), param.getIndex(), param.getIndex());
    }
}
