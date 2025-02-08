package org.flickit.assessment.core.application.service.attribute;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributesPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.flickit.assessment.core.application.port.in.attribute.GetAssessmentAttributesUseCase;

import java.util.List;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_ATTRIBUTES;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentAttributesService implements GetAssessmentAttributesUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAttributesPort loadAttributesPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;

    @Override
    public Result getAssessmentAttributes(Param param) {
/*        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_ATTRIBUTES))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);*/

        var portResult = loadAttributesPort.loadAttributes(param.getAssessmentId());
        return toResult(portResult);
    }

    public Result toResult(List<LoadAttributesPort.Result> portResult) {
        if (portResult.isEmpty())
            return new Result(null);

        List<Result.Attribute> attributes = portResult.stream()
            .map(this::toAttribute)
            .toList();

        return new Result(attributes);
    }

    private Result.Attribute toAttribute(LoadAttributesPort.Result portResult) {
        return new Result.Attribute(
            portResult.id(),
            portResult.title(),
            portResult.description(),
            portResult.index(),
            portResult.weight(),
            portResult.confidenceValue(),
            toMaturityLevel(portResult.maturityLevel()),
            toSubject(portResult.subject())
        );
    }

    private Result.MaturityLevel toMaturityLevel(LoadAttributesPort.MaturityLevel maturityLevel) {
        return new Result.MaturityLevel(
            maturityLevel.id(),
            maturityLevel.title(),
            maturityLevel.description(),
            maturityLevel.index(),
            maturityLevel.value()
        );
    }

    private Result.Subject toSubject(LoadAttributesPort.Subject subject) {
        return new Result.Subject(subject.id(), subject.title());
    }
}
