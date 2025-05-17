package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.out.assessmentresult.InvalidateAssessmentResultCalculatePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateAssessmentResultPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.flickit.assessment.core.common.ErrorMessageKey.MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ACTIVE_VERSION_NOT_FOUND;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResultWithoutActiveVersion;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MigrateAssessmentResultKitVersionHelperTest {

    @InjectMocks
    private MigrateAssessmentResultKitVersionHelper helper;

    @Mock
    private InvalidateAssessmentResultCalculatePort invalidateAssessmentResultCalculatePort;

    @Mock
    private UpdateAssessmentResultPort updateAssessmentResultPort;

    AssessmentResult assessmentResult = AssessmentResultMother.validResult();

    @Test
    void testMigrateAssessmentResultKitVersionService_ActiveKitVersionNotExists_ShouldThrowValidationException() {
        assessmentResult = validResultWithoutActiveVersion();

        var throwable = assertThrows(ValidationException.class, () -> helper.migrateKitVersion(assessmentResult));
        assertEquals(MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ACTIVE_VERSION_NOT_FOUND, throwable.getMessageKey());

        verifyNoInteractions(invalidateAssessmentResultCalculatePort, updateAssessmentResultPort);
    }

    @Test
    void testMigrateAssessmentResultKitVersionService_ValidParameters_SuccessfulUpdate() {
        var activeKitVersionId = assessmentResult.getAssessment().getAssessmentKit().getKitVersion();

        helper.migrateKitVersion(assessmentResult);

        verify(updateAssessmentResultPort, times(1)).updateKitVersionId(assessmentResult.getId(), activeKitVersionId);
        verify(invalidateAssessmentResultCalculatePort, times(1)).invalidateCalculate(assessmentResult.getId());
    }
}
