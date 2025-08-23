package org.flickit.assessment.core.application.service.maturitylevel;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.port.in.maturitylevel.GetAssessmentMaturityLevelsUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_MATURITY_LEVELS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_MATURITY_LEVELS_ASSESSMENT_RESULT_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentMaturityLevelsService implements GetAssessmentMaturityLevelsUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Override
    public Result getAssessmentMaturityLevels(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_MATURITY_LEVELS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_MATURITY_LEVELS_ASSESSMENT_RESULT_NOT_FOUND));

        var maturityLevels = loadMaturityLevelsPort.loadAllTranslated(assessmentResult).stream()
            .map(this::toResult)
            .toList();

        return new Result(maturityLevels);
    }

    private Result.MaturityLevel toResult(MaturityLevel maturityLevel) {
        return new Result.MaturityLevel(maturityLevel.getId(),
            maturityLevel.getTitle(),
            maturityLevel.getDescription(),
            maturityLevel.getIndex(),
            maturityLevel.getValue());
    }
}
