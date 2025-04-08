package org.flickit.assessment.kit.application.service.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.measure.UpdateMeasureUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional
@RequiredArgsConstructor
public class UpdateMeasureService implements UpdateMeasureUseCase {

    @Override
    public void updateMeasure(Param param) {

    }
}
