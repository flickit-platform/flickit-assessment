package org.flickit.assessment.kit.application.service.kitversion;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.domain.SubjectQuestionnaire;
import org.flickit.assessment.kit.application.port.in.kitversion.ActivateKitVersionUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UpdateKitActiveVersionPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.kitversion.UpdateKitVersionStatusPort;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.CreateSubjectQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.LoadSubjectQuestionnairePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.stream.Collectors.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.ACTIVATE_KIT_VERSION_STATUS_INVALID;

@Service
@Transactional
@RequiredArgsConstructor
public class ActivateKitVersionService implements ActivateKitVersionUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final UpdateKitVersionStatusPort updateKitVersionStatusPort;
    private final UpdateKitActiveVersionPort updateKitActiveVersionPort;
    private final LoadSubjectQuestionnairePort loadSubjectQuestionnairePort;
    private final CreateSubjectQuestionnairePort createSubjectQuestionnairePort;

    @Override
    public void activateKitVersion(Param param) {
        var kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        if (!KitVersionStatus.UPDATING.equals(kitVersion.getStatus()))
            throw new ValidationException(ACTIVATE_KIT_VERSION_STATUS_INVALID);

        var kit = kitVersion.getKit();
        var ownerId = loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (kit.getActiveVersionId() != null)
            updateKitVersionStatusPort.updateStatus(kit.getActiveVersionId(), KitVersionStatus.ARCHIVE);

        updateKitVersionStatusPort.updateStatus(param.getKitVersionId(), KitVersionStatus.ACTIVE);
        updateKitActiveVersionPort.updateActiveVersion(kit.getId(), param.getKitVersionId());

        var subjectQuestionnaires = loadSubjectQuestionnairePort.extractPairs(param.getKitVersionId()).stream()
            .collect(groupingBy(
                SubjectQuestionnaire::getQuestionnaireId,
                mapping(SubjectQuestionnaire::getSubjectId, toSet())));

        createSubjectQuestionnairePort.persistAll(subjectQuestionnaires, param.getKitVersionId());
    }
}
