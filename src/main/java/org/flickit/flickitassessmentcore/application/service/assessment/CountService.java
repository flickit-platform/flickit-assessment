package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CountUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CountAssessmentsByKitPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CountService implements CountUseCase {

    private final CountAssessmentsByKitPort countAssessmentsByKitPort;

    @Override
    public Result count(Param param) {
        var count = countAssessmentsByKitPort.count(
            param.getAssessmentKitId(),
            param.getIncludeDeleted(),
            param.getIncludeNotDeleted()
        );
        return new Result(count);
    }
}
