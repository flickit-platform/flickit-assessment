package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.RemoveAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.SoftDeleteAssessmentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RemoveAssessmentService implements RemoveAssessmentUseCase {

    private final SoftDeleteAssessmentPort deleteAssessmentPort;

    @Override
    public void removeAssessment(Param param) {
        deleteAssessmentPort.setDeletionTimeById(toParam(param));
    }

    private SoftDeleteAssessmentPort.Param toParam(Param param) {
        long deletionTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toInstant().toEpochMilli();
        return new SoftDeleteAssessmentPort.Param(param.getId(), deletionTime);
    }
}
