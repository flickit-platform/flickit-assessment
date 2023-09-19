package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.DeleteAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.SoftDeleteAssessmentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteAssessmentService implements DeleteAssessmentUseCase {

    private final SoftDeleteAssessmentPort deleteAssessmentPort;

    @Override
    public void deleteAssessment(Param param) {
        long deletionTime = generateDeletionTime();
        deleteAssessmentPort.setDeletionTimeById(param.getId(), deletionTime);
    }

    private long generateDeletionTime() {
        return ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
