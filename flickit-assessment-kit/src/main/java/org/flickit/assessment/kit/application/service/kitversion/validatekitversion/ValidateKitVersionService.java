package org.flickit.assessment.kit.application.service.kitversion.validatekitversion;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.common.exception.api.Notification;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.port.in.kitversion.ValidateKitVersionUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.VALIDATE_KIT_VERSION_STATUS_INVALID;

@Service
@RequiredArgsConstructor
public class ValidateKitVersionService implements ValidateKitVersionUseCase {

    private final List<KitVersionValidator> validators;
    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Override
    public Result validate(Param param) {
        var kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        if (!KitVersionStatus.UPDATING.equals(kitVersion.getStatus()))
            throw new ValidationException(VALIDATE_KIT_VERSION_STATUS_INVALID);

        var kit = kitVersion.getKit();
        var ownerId = loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        Notification finalResult = new Notification();
        validators.forEach(v -> {
            Notification result = v.validate(param.getKitVersionId());
            finalResult.merge(result);
        });
        return toResult(finalResult);
    }

    private Result toResult(Notification finalResult) {
        return new Result(!finalResult.hasErrors(), finalResult);
    }
}
