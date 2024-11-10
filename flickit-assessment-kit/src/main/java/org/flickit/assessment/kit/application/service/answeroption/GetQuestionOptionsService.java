package org.flickit.assessment.kit.application.service.answeroption;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.port.in.answeroption.GetQuestionOptionsUseCase;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionsByQuestionPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_QUESTION_OPTIONS_QUESTION_ANSWER_RANGE_ID_NOT_NULL;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetQuestionOptionsService implements GetQuestionOptionsUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadQuestionPort loadQuestionPort;
    private final LoadAnswerOptionsByQuestionPort loadAnswerOptionsByQuestionPort;

    @Override
    public Result getQuestionOptions(Param param) {
        KitVersion kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        if (!checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        Question question = loadQuestionPort.load(param.getQuestionId(), param.getKitVersionId());
        if(question.getAnswerRangeId() == null)
            throw new ValidationException(GET_QUESTION_OPTIONS_QUESTION_ANSWER_RANGE_ID_NOT_NULL);

        List<AnswerOption> answerOptions = loadAnswerOptionsByQuestionPort.loadByQuestionId(param.getQuestionId(),
            param.getKitVersionId());

        List<Result.Option> options = answerOptions.stream()
            .map(e -> new Result.Option(e.getId(), e.getTitle(), e.getIndex(), e.getValue()))
            .toList();

        return new Result(options);
    }
}
