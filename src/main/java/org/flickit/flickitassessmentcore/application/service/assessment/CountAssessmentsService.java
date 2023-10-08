package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CountAssessmentsUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CountAssessmentsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CountAssessmentsService implements CountAssessmentsUseCase {

    private final CountAssessmentsPort countAssessmentsPort;

    @Override
    public Result countAssessments(Param param) {
        var portParam = new CountAssessmentsPort.Param(param.getAssessmentKitId(), param.getDeleted(), param.getNotDeleted(), param.getTotal());
        var portResult = countAssessmentsPort.countByKitId(portParam);
        return new Result(portResult.totalCount(), portResult.deletedCount(), portResult.notDeletedCount());
    }
}
