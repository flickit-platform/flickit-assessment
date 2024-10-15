package org.flickit.assessment.kit.application.service.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributeOrdersUseCase;
import org.flickit.assessment.kit.application.port.out.attribute.UpdateAttributePort;
import org.flickit.assessment.kit.application.port.out.attribute.UpdateAttributePort.UpdateOrderParam;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateAttributeOrdersService implements UpdateAttributeOrdersUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final UpdateAttributePort updateAttributePort;

    @Override
    public void updateAttributeOrders(Param param) {
        var kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        var ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        updateAttributePort.updateOrders(toUpdatePortParam(param));
    }

    private UpdateOrderParam toUpdatePortParam(Param param) {
        var attributeOrders = param.getAttributes().stream()
            .map(e -> new UpdateOrderParam.AttributeOrder(e.getId(), e.getIndex()))
            .toList();
        return new UpdateOrderParam(attributeOrders, param.getKitVersionId(), LocalDateTime.now(), param.getCurrentUserId());
    }
}
