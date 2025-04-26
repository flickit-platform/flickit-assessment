package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.AssessmentKit;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.in.assessment.UpdateAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateAssessmentResultPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.UPDATE_ASSESSMENT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateAssessmentService implements UpdateAssessmentUseCase {

    private final UpdateAssessmentPort updateAssessmentPort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final UpdateAssessmentResultPort updateAssessmentResultPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAssessmentKitPort loadAssessmentKitPort;

    @Override
    public Result updateAssessment(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getId(), param.getCurrentUserId(), UPDATE_ASSESSMENT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getId())
            .orElseThrow(() -> new ResourceNotFoundException(UPDATE_ASSESSMENT_ASSESSMENT_RESULT_NOT_FOUND));

        var kit = loadAssessmentKitPort.loadAssessmentKit(assessmentResult.getAssessment().getAssessmentKit().getId(), null)
            .orElseThrow(() -> new ResourceNotFoundException(UPDATE_ASSESSMENT_ASSESSMENT_KIT_NOT_FOUND));

        if (param.getLang() != null)
            handleLanguageUpdate(param.getLang(), kit, assessmentResult);

        String code = generateSlugCode(param.getTitle());
        LocalDateTime lastModificationTime = LocalDateTime.now();
        UpdateAssessmentPort.AllParam updateParam = new UpdateAssessmentPort.AllParam(
            param.getId(),
            param.getTitle(),
            param.getShortTitle(),
            code,
            lastModificationTime,
            param.getCurrentUserId());

        return new Result(updateAssessmentPort.update(updateParam).id());
    }

    private void handleLanguageUpdate(String lang, AssessmentKit kit, AssessmentResult assessmentResult) {
        KitLanguage newLanguage = KitLanguage.valueOf(lang);
        if (!kit.getSupportedLanguages().contains(newLanguage))
            throw new ValidationException(UPDATE_ASSESSMENT_LANGUAGE_NOT_SUPPORTED);

        KitLanguage oldLanguage = assessmentResult.getLanguage();
        if (!newLanguage.equals(oldLanguage))
            updateAssessmentResultPort.updateLanguage(assessmentResult.getId(), newLanguage);
    }
}
