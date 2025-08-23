package org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationsException;
import org.flickit.assessment.common.exception.api.Notification;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UpdateKitByDslUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitFullInfoPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UpdateKitLastMajorModificationTimePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.LoadDslJsonPathPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.UpdateKitDslPort;
import org.flickit.assessment.kit.application.port.out.minio.LoadKitDSLJsonFilePort;
import org.flickit.assessment.kit.application.service.DslTranslator;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.validate.CompositeUpdateKitValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.adapter.in.rest.exception.api.ErrorCodes.UNSUPPORTED_DSL_CONTENT_CHANGE;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UpdateKitByDslService implements UpdateKitByDslUseCase {

    private final LoadAssessmentKitFullInfoPort loadAssessmentKitFullInfoPort;
    private final LoadDslJsonPathPort loadDslJsonPathPort;
    private final LoadKitDSLJsonFilePort loadKitDSLJsonFilePort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final CompositeUpdateKitValidator validator;
    private final CompositeUpdateKitPersister persister;
    private final UpdateKitLastMajorModificationTimePort updateKitLastMajorModificationTimePort;
    private final UpdateKitDslPort updateKitDslPort;

    @Override
    public void update(Param param) {
        AssessmentKit savedKit = loadAssessmentKitFullInfoPort.load(param.getKitId());
        String dslJsonPath = loadDslJsonPathPort.loadJsonPath(param.getKitDslId());
        String dslContent = loadKitDSLJsonFilePort.loadDslJson(dslJsonPath);
        AssessmentKitDslModel dslKit = DslTranslator.parseJson(dslContent);
        UUID currentUserId = param.getCurrentUserId();

        validateUserIsExpertGroupOwner(savedKit.getExpertGroupId(), currentUserId);
        validateChanges(savedKit, dslKit);
        UpdateKitPersisterResult persistResult = persister.persist(savedKit, dslKit, currentUserId);
        if (persistResult.isMajorUpdate())
            updateKitLastMajorModificationTimePort.updateLastMajorModificationTime(savedKit.getId(), LocalDateTime.now());

        updateKitDslPort.update(param.getKitDslId(), param.getKitId(), param.getCurrentUserId(), LocalDateTime.now());
    }

    private void validateUserIsExpertGroupOwner(long expertGroupId, UUID currentUserId) {
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId);
        if (!ownerId.equals(currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private void validateChanges(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        Notification notification = validator.validate(savedKit, dslKit);
        if (notification.hasErrors())
            throw new ValidationsException(UNSUPPORTED_DSL_CONTENT_CHANGE, notification);
    }
}
