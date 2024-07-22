package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.domain.User;
import org.flickit.assessment.kit.application.port.in.assessmentkit.DeleteKitUserAccessUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.CheckKitUserAccessPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.DeleteKitUserAccessPort;
import org.flickit.assessment.kit.application.port.out.user.LoadUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DeleteKitUserAccessService implements DeleteKitUserAccessUseCase {

    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final CheckKitUserAccessPort checkKitUserAccessPort;
    private final DeleteKitUserAccessPort deleteKitUserAccessPort;
    private final LoadUserPort loadUserPort;

    @Override
    public void delete(Param param) {
        ExpertGroup expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId());
        validateCurrentUser(expertGroup, param.getCurrentUserId());
        checkAccessExistence(param);
        checkAccessNotBelongsToExpertGroupOwner(param, expertGroup.getOwnerId());

        deleteKitUserAccessPort.delete(new DeleteKitUserAccessPort.Param(param.getKitId(), param.getUserId()));
        log.debug("User [{}] access to private kit [{}] is removed.", param.getCurrentUserId(), param.getCurrentUserId());
    }

    private void checkAccessNotBelongsToExpertGroupOwner(Param param, UUID ownerId) {
        if (Objects.equals(param.getUserId(), ownerId))
            throw new ValidationException(DELETE_KIT_USER_ACCESS_USER_OWNER_DELETION_NOT_ALLOWED);
    }

    private void validateCurrentUser(ExpertGroup expertGroup, UUID currentUserId) {
        if (!Objects.equals(expertGroup.getOwnerId(), currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private void checkAccessExistence(Param param) {
        User user = loadUserPort.loadById(param.getUserId()).orElseThrow(
            () -> new ResourceNotFoundException(DELETE_KIT_USER_ACCESS_USER_NOT_FOUND));
        if (!checkKitUserAccessPort.hasAccess(param.getKitId(), user.getId()))
            throw new ResourceNotFoundException(DELETE_KIT_USER_ACCESS_KIT_USER_NOT_FOUND);
    }
}
