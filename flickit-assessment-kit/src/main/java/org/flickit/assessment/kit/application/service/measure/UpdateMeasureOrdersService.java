package org.flickit.assessment.kit.application.service.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.measure.UpdateMeasureOrdersUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateMeasureOrdersService implements UpdateMeasureOrdersUseCase {

    @Override
    public void changeOrders(Param param) {

    }
}
