package org.flickit.assessment.kit.application.service.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.Measure;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.port.in.questionnaire.CreateQuestionnaireUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.measure.CreateMeasurePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.CreateQuestionnairePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.util.GenerateHashCodeUtil.generateCode;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateQuestionnaireService implements CreateQuestionnaireUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final CreateQuestionnairePort createQuestionnairePort;
    private final CreateMeasurePort createMeasurePort;

    @Override
    public long createQuestionnaire(Param param) {
        KitVersion kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        Questionnaire questionnaire = new Questionnaire(null,
            generateCode(param.getTitle()),
            param.getTitle(),
            param.getIndex(),
            param.getDescription(),
            LocalDateTime.now(),
            LocalDateTime.now());

        createMeasurePort.persist(convertToMeasure(questionnaire), param.getKitVersionId(), param.getCurrentUserId());
        return createQuestionnairePort.persist(questionnaire, param.getKitVersionId(), param.getCurrentUserId());
    }

    private Measure convertToMeasure(Questionnaire questionnaire) {
        return new Measure(null,
            questionnaire.getCode(),
            questionnaire.getTitle(),
            questionnaire.getIndex(),
            questionnaire.getDescription(),
            questionnaire.getCreationTime(),
            questionnaire.getLastModificationTime());
    }
}
