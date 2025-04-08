package org.flickit.assessment.kit.application.service.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.measure.UpdateMeasureOrdersUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.measure.UpdateMeasurePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateMeasureOrdersService implements UpdateMeasureOrdersUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final UpdateMeasurePort updateMeasurePort;

    @Override
    public void changeOrders(Param param) {
        var kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        var ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        updateMeasurePort.updateOrders(toUpdateParam(param));
    }

    private UpdateMeasurePort.UpdateOrderParam toUpdateParam(Param param) {
        var measureOrders = param.getOrders().stream()
            .map(e -> new UpdateMeasurePort.UpdateOrderParam.MeasureOrder(e.getId(), e.getIndex()))
            .toList();

        return new UpdateMeasurePort.UpdateOrderParam(measureOrders,
            param.getKitVersionId(),
            LocalDateTime.now(),
            param.getCurrentUserId());
    }
}
