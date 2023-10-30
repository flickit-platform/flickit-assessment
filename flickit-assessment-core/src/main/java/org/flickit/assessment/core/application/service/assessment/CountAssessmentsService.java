package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.assessment.CountAssessmentsUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CountAssessmentsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CountAssessmentsService implements CountAssessmentsUseCase {

    private final CountAssessmentsPort countAssessmentsPort;

    @Override
    public Result countAssessments(Param param) {
        var portParam = new CountAssessmentsPort.Param(
            param.getAssessmentKitId(),
            param.getSpaceId(),
            param.isDeleted(),
            param.isNotDeleted(),
            param.isTotal());
        var portResult = countAssessmentsPort.count(portParam);
        return new Result(portResult.totalCount(), portResult.deletedCount(), portResult.notDeletedCount());
    }
}
