package org.flickit.assessment.kit.application.service.answeroption;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.AnswerRange;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.port.in.answeroption.CreateAnswerOptionUseCase;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answerrange.CreateAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.answerrange.LoadAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.CREATE_ANSWER_OPTION_ANSWER_RANGE_REUSABLE;


@Service
@Transactional
@RequiredArgsConstructor
public class CreateAnswerOptionService implements CreateAnswerOptionUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final LoadQuestionPort loadQuestionPort;
    private final CreateAnswerRangePort createAnswerRangePort;
    private final UpdateQuestionPort updateQuestionPort;
    private final LoadAnswerRangePort loadAnswerRangePort;
    private final CreateAnswerOptionPort createAnswerOptionPort;

    @Override
    public Result createAnswerOption(Param param) {
        checkUserAccess(param);

        Question question = loadQuestionPort.load(param.getQuestionId(), param.getKitVersionId());
        Long questionAnswerRangeId = question.getAnswerRangeId();
        if (questionAnswerRangeId == null) {
            questionAnswerRangeId = createAnswerRangePort.persist(tocreateAnswerRangePortParam(param));
            updateQuestionPort.updateAnswerRange(toUpdateQuestionPortParam(param, question, questionAnswerRangeId));
        } else {
            AnswerRange answerRange = loadAnswerRangePort.load(questionAnswerRangeId, param.getKitVersionId());
            if (answerRange.isReusable())
                throw new ValidationException(CREATE_ANSWER_OPTION_ANSWER_RANGE_REUSABLE);
        }

        long answerOptionId = createAnswerOptionPort.persist(toCreateParam(param, questionAnswerRangeId));
        return new Result(answerOptionId);
    }

    private void checkUserAccess(Param param) {
        KitVersion kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private CreateAnswerRangePort.Param tocreateAnswerRangePortParam(Param param) {
        return new CreateAnswerRangePort.Param(param.getKitVersionId(), null, null, false, param.getCurrentUserId());
    }

    private UpdateQuestionPort.UpdateAnswerRangeParam toUpdateQuestionPortParam(Param param, Question question, long answerRangeId) {
        return new UpdateQuestionPort.UpdateAnswerRangeParam(question.getId(),
            param.getKitVersionId(),
            answerRangeId,
            LocalDateTime.now(),
            param.getCurrentUserId());
    }

    private CreateAnswerOptionPort.Param toCreateParam(Param param, Long answerRangeId) {
        return new CreateAnswerOptionPort.Param(
            param.getTitle(),
            param.getIndex(),
            answerRangeId,
            param.getValue(),
            param.getKitVersionId(),
            param.getCurrentUserId());
    }
}
