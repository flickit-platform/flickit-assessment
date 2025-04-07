package org.flickit.assessment.kit.application.service.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.measure.GetMeasuresUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetMeasuresService implements GetMeasuresUseCase {

    @Override
    public PaginatedResponse<MeasureListItem> getMeasures(Param param) {
        return null;
    }
}
