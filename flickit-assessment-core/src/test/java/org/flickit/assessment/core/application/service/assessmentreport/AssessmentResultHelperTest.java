package org.flickit.assessment.core.application.service.assessmentreport;

import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadKitLastMajorModificationTimePort;
import org.flickit.assessment.core.application.port.out.kitcustom.LoadKitCustomLastModificationTimePort;
import org.flickit.assessment.core.application.service.assessment.AssessmentResultHelper;
import org.flickit.assessment.core.application.service.assessment.CalculateAssessmentHelper;
import org.flickit.assessment.core.application.service.assessment.CalculateConfidenceHelper;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentResultHelperTest {

    @InjectMocks
    private AssessmentResultHelper assessmentResultHelper;

    @Mock
    private LoadKitLastMajorModificationTimePort loadKitLastMajorModificationTimePort;

    @Mock
    private LoadKitCustomLastModificationTimePort loadKitCustomLastModificationTimePort;

    @Mock
    private CalculateAssessmentHelper calculateAssessmentHelper;

    @Mock
    private CalculateConfidenceHelper calculateConfidenceHelper;

    private AssessmentResult assessmentResult = AssessmentResultMother.validResultWithKitCustomId(null);

    @Test
    void assessmentResultHelperTest_whenRecalculatingIsNotNeeded_thenNotRecalculate() {
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(assessmentResult.getAssessment().getAssessmentKit().getId()))
            .thenReturn(assessmentResult.getLastCalculationTime());

        assessmentResultHelper.recalculateAssessmentResultIfRequired(assessmentResult);

        verifyNoInteractions(loadKitCustomLastModificationTimePort,
            calculateAssessmentHelper,
            calculateConfidenceHelper);
    }

    @Test
    void assessmentResultHelperTest_whenAssessmentRecalculatingIsNeeded_thenRecalculate() {
        var kitLastMajorModificationTime = assessmentResult.getLastCalculationTime();
        assessmentResult.setIsCalculateValid(false);

        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(assessmentResult.getAssessment().getAssessmentKit().getId()))
            .thenReturn(assessmentResult.getLastCalculationTime());

        assessmentResultHelper.recalculateAssessmentResultIfRequired(assessmentResult);

        verify(calculateAssessmentHelper).calculateMaturityLevel(assessmentResult, kitLastMajorModificationTime);
        verifyNoInteractions(loadKitCustomLastModificationTimePort,
            calculateConfidenceHelper);
    }

    @Test
    void assessmentResultHelperTest_whenKitCustomExistsAndAndConfidenceRecalculatingIsNeeded_thenRecalculate() {
        var kitCustomId = 123L;
        assessmentResult = AssessmentResultMother.validResultWithKitCustomId(kitCustomId);
        assessmentResult.setIsConfidenceValid(false);
        LocalDateTime kitLastMajorModificationTime = assessmentResult.getLastCalculationTime();

        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(assessmentResult.getAssessment().getAssessmentKit().getId()))
            .thenReturn(kitLastMajorModificationTime);
        when(loadKitCustomLastModificationTimePort.loadLastModificationTime(kitCustomId))
            .thenReturn(assessmentResult.getLastCalculationTime().plusDays(1));

        assessmentResultHelper.recalculateAssessmentResultIfRequired(assessmentResult);

        verify(calculateConfidenceHelper).calculate(assessmentResult, kitLastMajorModificationTime);
        verify(calculateAssessmentHelper).calculateMaturityLevel(assessmentResult, kitLastMajorModificationTime);
    }

    @Test
    void assessmentResultHelperTest_whenKitCustomExistsAndAndConfidenceRecalculatingIsNeeded_thenRecalculate1() {
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(assessmentResult.getAssessment().getAssessmentKit().getId()))
            .thenReturn(assessmentResult.getLastCalculationTime());

        assessmentResultHelper.recalculateAssessmentResultIfRequired(assessmentResult);

        verifyNoInteractions(loadKitCustomLastModificationTimePort,
            calculateAssessmentHelper,
            calculateConfidenceHelper );
    }
}
