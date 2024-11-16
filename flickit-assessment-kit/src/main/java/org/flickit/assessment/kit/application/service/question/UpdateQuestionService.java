package org.flickit.assessment.kit.application.service.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.port.in.question.UpdateQuestionUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CountKitAssessmentsPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_QUESTION_ANSWER_RANGE_ID_NOT_UPDATABLE;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateQuestionService implements UpdateQuestionUseCase {

    private final UpdateQuestionPort updateQuestionPort;
    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final LoadQuestionPort loadQuestionPort;
    private final CountKitAssessmentsPort countKitAssessmentsPort;

    @Override
    public void updateQuestion(Param param) {
        KitVersion kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        Question question = loadQuestionPort.load(param.getQuestionId(), param.getKitVersionId());
        if (question.getAnswerRangeId() != null
            && !Objects.equals(question.getAnswerRangeId(), param.getAnswerRangeId())
            && countKitAssessmentsPort.count(kitVersion.getKit().getId()) > 0)
            throw new ValidationException(UPDATE_QUESTION_ANSWER_RANGE_ID_NOT_UPDATABLE);

        String code = Question.generateCode(param.getIndex());
        updateQuestionPort.update(new UpdateQuestionPort.Param(param.getQuestionId(),
            param.getKitVersionId(),
            code,
            param.getTitle(),
            param.getIndex(),
            param.getHint(),
            param.getMayNotBeApplicable(),
            param.getAdvisable(),
            param.getAnswerRangeId(),
            LocalDateTime.now(),
            param.getCurrentUserId()));
    }
}
