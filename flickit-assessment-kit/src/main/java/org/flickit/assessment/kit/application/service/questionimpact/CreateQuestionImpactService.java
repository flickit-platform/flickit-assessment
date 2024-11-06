package org.flickit.assessment.kit.application.service.questionimpact;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.domain.QuestionImpact;
import org.flickit.assessment.kit.application.port.in.questionimpact.CreateQuestionImpactUseCase;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionsByQuestionPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.CreateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.CreateQuestionImpactPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateQuestionImpactService implements CreateQuestionImpactUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final CreateQuestionImpactPort createQuestionImpactPort;
    private final LoadAnswerOptionsByQuestionPort loadAnswerOptionsByQuestionPort;
    private final CreateAnswerOptionImpactPort createAnswerOptionImpactPort;

    @Override
    public long createQuestionImpact(Param param) {
        Long kitVersionId = param.getKitVersionId();
        var kitversion = loadKitVersionPort.load(kitVersionId);
        var expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(kitversion.getKit().getExpertGroupId());

        UUID currentUserId = param.getCurrentUserId();
        if (!expertGroupOwnerId.equals(currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        Long questionImpactId = createQuestionImpactPort.persist(toQuestionImpact(param));

        var answerOptions = loadAnswerOptionsByQuestionPort.loadByQuestionId(param.getQuestionId(), kitVersionId);
        List<Long> optionIds = answerOptions.stream()
            .map(AnswerOption::getId)
            .toList();

        List<CreateAnswerOptionImpactPort.Param> optionImpacts = optionIds.stream()
            .map(optionId -> new CreateAnswerOptionImpactPort.Param(questionImpactId,
                optionId,
                null,
                kitVersionId,
                currentUserId))
            .toList();
        createAnswerOptionImpactPort.persistAll(optionImpacts);

        return questionImpactId;
    }

    private QuestionImpact toQuestionImpact(Param param) {
        return new QuestionImpact(null,
            param.getAttributeId(),
            param.getMaturityLevelId(),
            param.getWeight(),
            param.getKitVersionId(),
            param.getQuestionId(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            param.getCurrentUserId(),
            param.getCurrentUserId());
    }
}
