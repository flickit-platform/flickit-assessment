package org.flickit.assessment.kit.application.service.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.port.in.question.DeleteQuestionUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.question.DeleteQuestionPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_QUESTION_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteQuestionService implements DeleteQuestionUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final LoadQuestionPort loadQuestionPort;
    private final DeleteQuestionPort deleteQuestionPort;
    private final UpdateQuestionPort updateQuestionPort;

    @Override
    public void deleteQuestion(Param param) {
        var kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        var expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());
        if (!expertGroupOwnerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (!kitVersion.getStatus().equals(KitVersionStatus.UPDATING))
            throw new ValidationException(DELETE_QUESTION_NOT_ALLOWED);

        Question question = loadQuestionPort.load(param.getQuestionId(), param.getKitVersionId());
        deleteQuestionPort.delete(param.getQuestionId(), param.getKitVersionId());
        updateQuestionPort.reindexQuestionsAfter(question.getIndex(), question.getQuestionnaireId(), param.getKitVersionId());
    }
}
