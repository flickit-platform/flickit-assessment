package org.flickit.assessment.core.application.service.assessmentreport;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.assessmentreport.CreateAssessmentReportMetadataUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAssessmentReportMetadataService implements CreateAssessmentReportMetadataUseCase {

    @Override
    public void createReportMetadata(Param param) {

    }

    private boolean containsNonNullParam(Param param) {
        var metadata = param.getMetadata();
        return Objects.nonNull(metadata.getIntro()) ||
            Objects.nonNull(metadata.getProsAndCons()) ||
            Objects.nonNull(metadata.getSteps()) ||
            Objects.nonNull(metadata.getParticipants());
    }
}
