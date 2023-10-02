package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CountAssessmentsUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CountAssessmentsByKitPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CountAssessmentsService implements CountAssessmentsUseCase {

    private final CountAssessmentsByKitPort countAssessmentsByKitPort;

    @Override
    public Result countAssessments(Param param) {
        var portParam = new CountAssessmentsByKitPort.Param(param.getAssessmentKitId(), param.getDeleted(), param.getNotDeleted(), param.getTotal());
        var portResult = countAssessmentsByKitPort.count(portParam);
        return new Result(portResult.totalCount(), portResult.deletedCount(), portResult.notDeletedCount());
    }
}
