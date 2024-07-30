package org.flickit.assessment.kit.application.service.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.port.in.questionnaire.CreateQuestionnaireUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.CreateQuestionnairePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateQuestionnaireService implements CreateQuestionnaireUseCase {

    private final CreateQuestionnairePort createQuestionnairePort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final LoadAssessmentKitPort loadAssessmentKitPort;

    @Override
    public long createQuestionnaire(Param param) {
        AssessmentKit kit = loadAssessmentKitPort.load(param.getKitId());
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        Questionnaire questionnaire = new Questionnaire(null,
            Questionnaire.generateSlugCode(param.getTitle()),
            param.getTitle(),
            param.getIndex(),
            param.getDescription(),
            LocalDateTime.now(),
            LocalDateTime.now());
        return createQuestionnairePort.persist(questionnaire, kit.getKitVersionId(), param.getCurrentUserId());
    }
}
