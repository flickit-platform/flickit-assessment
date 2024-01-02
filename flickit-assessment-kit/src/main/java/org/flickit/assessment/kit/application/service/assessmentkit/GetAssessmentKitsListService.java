package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetAssessmentKitsListUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitsListPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentKitsListService implements GetAssessmentKitsListUseCase {

    private final LoadAssessmentKitsListPort loadKitsListPort;

    @Override
        public PaginatedResponse<KitsListItem> getKitsList(Param param) {
        return loadKitsListPort.loadKitsList(toPortParam(param));
    }

    private LoadAssessmentKitsListPort.Param toPortParam(Param param) {
        return new LoadAssessmentKitsListPort.Param(
            param.getIsPrivate(),
            param.getPage(),
            param.getSize(),
            param.getCurrentUserId()
        );
    }
}
