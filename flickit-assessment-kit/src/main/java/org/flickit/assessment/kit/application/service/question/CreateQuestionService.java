package org.flickit.assessment.kit.application.service.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.port.in.question.CreateQuestionUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.question.CreateQuestionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateQuestionService implements CreateQuestionUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final CreateQuestionPort createQuestionPort;

    @Override
    public long createQuestion(Param param) {
        KitVersion kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        return createQuestionPort.persist(toParam(param));
    }

    private CreateQuestionPort.Param toParam(Param param) {
        return new CreateQuestionPort.Param(Question.generateCode(param.getIndex()),
            param.getTitle(),
            param.getIndex(),
            param.getHint(),
            param.getMayNotBeApplicable(),
            param.getAdvisable(),
            param.getKitVersionId(),
            param.getQuestionnaireId(),
            null,
            null,
            param.getTranslations(),
            param.getCurrentUserId());
    }
}
