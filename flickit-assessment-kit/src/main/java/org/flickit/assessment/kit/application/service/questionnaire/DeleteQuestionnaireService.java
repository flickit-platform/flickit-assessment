package org.flickit.assessment.kit.application.service.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.port.in.questionnaire.DeleteQuestionnaireUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.measure.DeleteMeasurePort;
import org.flickit.assessment.kit.application.port.out.measure.LoadMeasurePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.DeleteQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_QUESTIONNAIRE_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteQuestionnaireService implements DeleteQuestionnaireUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final LoadQuestionnairePort loadQuestionnairePort;
    private final LoadMeasurePort loadMeasurePort;
    private final DeleteQuestionnairePort deleteQuestionnairePort;
    private final DeleteMeasurePort deleteMeasurePort;

    @Override
    public void deleteQuestionnaire(Param param) {
        KitVersion kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (!KitVersionStatus.UPDATING.equals(kitVersion.getStatus()))
            throw new ValidationException(DELETE_QUESTIONNAIRE_NOT_ALLOWED);

        var questionnaire = loadQuestionnairePort.load(param.getKitVersionId(), param.getQuestionnaireId());
        var measure = loadMeasurePort.loadByCode(param.getKitVersionId(), questionnaire.getCode());

        deleteQuestionnairePort.delete(param.getQuestionnaireId(), param.getKitVersionId());
        deleteMeasurePort.delete(measure.getId(), param.getKitVersionId());
    }
}
