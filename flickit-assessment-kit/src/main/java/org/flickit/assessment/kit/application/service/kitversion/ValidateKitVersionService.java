package org.flickit.assessment.kit.application.service.kitversion;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.port.in.kitversion.ValidateKitVersionUseCase;
import org.flickit.assessment.kit.application.port.out.answerrange.LoadAnswerRangesPort;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAllAttributesPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionsPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectsPort;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@Service
@RequiredArgsConstructor
public class ValidateKitVersionService implements ValidateKitVersionUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final LoadQuestionsPort loadQuestionsPort;
    private final LoadAnswerRangesPort loadAnswerRangesPort;
    private final LoadSubjectsPort loadSubjectsPort;
    private final LoadAllAttributesPort loadAllAttributesPort;

    @Override
    public Result validate(Param param) {
        var kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        var ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        if (!KitVersionStatus.UPDATING.equals(kitVersion.getStatus()))
            throw new ValidationException(VALIDATE_KIT_VERSION_STATUS_INVALID);

        List<String> errors = new LinkedList<>();
        errors.addAll(loadQuestionsPort.loadQuestionsWithoutImpact(param.getKitVersionId())
            .stream()
            .map(e -> MessageBundle.message(VALIDATE_KIT_VERSION_QUESTION_IMPACT_NOT_NULL, e.questionIndex(), e.questionnaireTitle()))
            .toList());

        errors.addAll(loadQuestionsPort.loadQuestionsWithoutAnswerRange(param.getKitVersionId())
            .stream()
            .map(e -> MessageBundle.message(VALIDATE_KIT_VERSION_QUESTION_ANSWER_RANGE_NOT_NULL, e.questionIndex(), e.questionnaireTitle()))
            .toList());

        errors.addAll(loadAnswerRangesPort.loadByKitVersionIdWithoutAnswerOptions(param.getKitVersionId())
            .stream()
            .map(e -> MessageBundle.message(VALIDATE_KIT_VERSION_ANSWER_RANGE_ANSWER_OPTION_NOT_NULL, e.getTitle()))
            .toList());

        errors.addAll(loadAllAttributesPort.loadByKitVersionIdAndQuestionsWithoutImpact(param.getKitVersionId())
            .stream()
            .map(e -> MessageBundle.message(VALIDATE_KIT_VERSION_ATTRIBUTE_QUESTION_IMPACT_NOT_NULL, e.getTitle()))
            .toList());

        errors.addAll(loadSubjectsPort.loadByKitVersionIdWithoutAttribute(param.getKitVersionId())
            .stream()
            .map(e -> MessageBundle.message(VALIDATE_KIT_VERSION_SUBJECT_ATTRIBUTE_NOT_NULL, e.getTitle()))
            .toList());

        return toResult(errors);
    }

    private Result toResult(List<String> errors) {
        return new Result(errors.isEmpty(), errors);
    }
}
