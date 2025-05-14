package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.out.assessmentresult.InvalidateAssessmentResultCalculatePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateAssessmentResultPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ACTIVE_VERSION_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ASSESSMENT_RESULT_ID_NOT_FOUND;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResultWithoutActiveVersion;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MigrateAssessmentResultKitVersionHelperTest {

    @InjectMocks
    private MigrateAssessmentResultKitVersionHelper helper;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private InvalidateAssessmentResultCalculatePort invalidateAssessmentResultCalculatePort;

    @Mock
    private UpdateAssessmentResultPort updateAssessmentResultPort;

    AssessmentResult assessmentResult = AssessmentResultMother.validResult();

    @Test
    void testMigrateAssessmentResultKitVersionService_AssessmentResultNotExists_ShouldThrowResourceNotFoundException() {
        var assessmentId = UUID.randomUUID();

        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> helper.migrateKitVersion(assessmentId));
        assertEquals(MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ASSESSMENT_RESULT_ID_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(invalidateAssessmentResultCalculatePort, updateAssessmentResultPort);
    }

    @Test
    void testMigrateAssessmentResultKitVersionService_ActiveKitVersionNotExists_ShouldThrowValidationException() {
        assessmentResult = validResultWithoutActiveVersion();

        when(loadAssessmentResultPort.loadByAssessmentId(assessmentResult.getAssessment().getId())).thenReturn(Optional.of(assessmentResult));

        var throwable = assertThrows(ValidationException.class, () -> helper.migrateKitVersion(assessmentResult.getAssessment().getId()));
        assertEquals(MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ACTIVE_VERSION_NOT_FOUND, throwable.getMessageKey());

        verifyNoInteractions(invalidateAssessmentResultCalculatePort, updateAssessmentResultPort);
    }

    @Test
    void testMigrateAssessmentResultKitVersionService_ValidParameters_SuccessfulUpdate() {
        var activeKitVersionId = assessmentResult.getAssessment().getAssessmentKit().getKitVersion();

        when(loadAssessmentResultPort.loadByAssessmentId(assessmentResult.getAssessment().getId())).thenReturn(Optional.of(assessmentResult));

        helper.migrateKitVersion(assessmentResult.getAssessment().getId());

        verify(updateAssessmentResultPort, times(1)).updateKitVersionId(assessmentResult.getId(), activeKitVersionId);
        verify(invalidateAssessmentResultCalculatePort, times(1)).invalidateCalculate(assessmentResult.getId());
    }
}
