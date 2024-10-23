package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.port.in.assessment.MigrateAssessmentResultKitVersionUseCase;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.InvalidateAssessmentResultCalculatePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateAssessmentResultPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.MIGRATE_KIT_VERSION;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Service
@Transactional
@RequiredArgsConstructor
public class MigrateAssessmentResultKitVersionService implements MigrateAssessmentResultKitVersionUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final GetAssessmentPort getAssessmentPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final InvalidateAssessmentResultCalculatePort loadAssessmentResultCalculatePort;
    private final UpdateAssessmentResultPort updateAssessmentResultPort;

    @Override
    public void migrateKitVersion(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MIGRATE_KIT_VERSION))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessment = getAssessmentPort.getAssessmentById(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ASSESSMENT_ID_NOT_FOUND));

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(assessment.getId())
            .orElseThrow(() -> new ResourceNotFoundException(MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ASSESSMENT_RESULT_ID_NOT_FOUND));

        loadAssessmentResultCalculatePort.invalidateCalculate(assessmentResult.getId());

        var activeKitVersionId = assessment.getAssessmentKit().getKitVersion();
        updateAssessmentResultPort.updateKitVersionId(assessmentResult.getId(), activeKitVersionId);

        if (activeKitVersionId == null)
            throw new ValidationException(MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ACTIVE_VERSION_NOT_FOUND);
    }
}
