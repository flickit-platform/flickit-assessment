package org.flickit.assessment.kit.application.service.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.port.in.questionnaire.UpdateQuestionnaireUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.UpdateQuestionnairePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateQuestionnaireService implements UpdateQuestionnaireUseCase {

    private final UpdateQuestionnairePort updateQuestionnairePort;
    private final LoadAssessmentKitPort loadAssessmentKitPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Override
    public void updateQuestionnaire(Param param) {
        AssessmentKit kit = loadAssessmentKitPort.load(param.getKitId());
        Long kitVersionId = kit.getKitVersionId();
        long expertGroupId = kit.getExpertGroupId();
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId);
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        String code = Questionnaire.generateSlugCode(param.getTitle());
        updateQuestionnairePort.update(new UpdateQuestionnairePort.Param(param.getQuestionnaireId(),
            kitVersionId,
            param.getTitle(),
            code,
            param.getIndex(),
            param.getDescription(),
            LocalDateTime.now(),
            param.getCurrentUserId()));
    }
}
