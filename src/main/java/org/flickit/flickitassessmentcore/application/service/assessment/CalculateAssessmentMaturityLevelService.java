package org.flickit.flickitassessmentcore.application.service.assessment;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CalculateAssessmentMaturityLevelUseCase;
import org.flickit.flickitassessmentcore.application.port.out.LoadAssessmentKitPort;
import org.flickit.flickitassessmentcore.application.port.out.LoadAssessmentPort;
import org.flickit.flickitassessmentcore.domain.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
public class CalculateAssessmentMaturityLevelService implements CalculateAssessmentMaturityLevelUseCase {

    private final LoadAssessmentPort loadAssessment;
    private final LoadAssessmentKitPort loadAssessmentKitPort;

    @Override
    public MaturityLevel calculateAssessmentMaturityLevel(UUID assessmentId) {
        Assessment assessment = loadAssessment.loadAssessment(assessmentId);
//        Set<AssessmentResult> assessmentResults = assessment.getAssessmentResults();
        AssessmentKit assessmentKit = loadAssessmentKitPort.loadAssessmentKit(assessment.getAssessmentKit().getId());
//        Set<AssessmentSubject> subjects = assessmentKit.getAssessmentSubjects();
//        subjects.forEach(subject -> {
            // CALCULATE SUBJECT MATURITY LEVEL
            // THEN ASSESSMENT MATURITY LEVEL
//        });

        return new MaturityLevel();
    }


}
