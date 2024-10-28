package org.flickit.assessment.kit.application.service.answeroption;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.answeroption.GetQuestionOptionsUseCase;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionsByQuestionPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetQuestionOptionsService implements GetQuestionOptionsUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadAnswerOptionsByQuestionPort loadAnswerOptionsByQuestionPort;

    @Override
    public Result getQuestionOptions(Param param) {
        KitVersion kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        if (!checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        List<AnswerOption> answerOptions = loadAnswerOptionsByQuestionPort.loadByQuestionId(param.getQuestionId(),
            param.getKitVersionId());

        List<Result.Option> options = answerOptions.stream()
            .map(e -> new Result.Option(e.getId(), e.getTitle(), e.getIndex()))
            .toList();

        return new Result(options);
    }
}
