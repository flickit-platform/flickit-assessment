package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.port.out.assessmentresult.InvalidateAssessmentResultCalculatePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateAssessmentResultPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Service
@Transactional
@RequiredArgsConstructor
public class MigrateAssessmentResultKitVersionHelper {

    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final InvalidateAssessmentResultCalculatePort loadAssessmentResultCalculatePort;
    private final UpdateAssessmentResultPort updateAssessmentResultPort;

    public void migrateKitVersion(UUID assessmentId) {
        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ASSESSMENT_RESULT_ID_NOT_FOUND));

        var activeKitVersionId = assessmentResult.getAssessment().getAssessmentKit().getKitVersion();
        if (activeKitVersionId == null)
            throw new ValidationException(MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ACTIVE_VERSION_NOT_FOUND);

        updateAssessmentResultPort.updateKitVersionId(assessmentResult.getId(), activeKitVersionId);
        loadAssessmentResultCalculatePort.invalidateCalculate(assessmentResult.getId());
    }
}
