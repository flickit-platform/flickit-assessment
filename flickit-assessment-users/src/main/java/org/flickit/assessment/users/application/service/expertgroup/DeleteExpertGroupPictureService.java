package org.flickit.assessment.users.application.service.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.port.in.expertgroup.DeleteExpertGroupPictureUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.*;
import org.flickit.assessment.users.application.port.out.minio.DeleteFilePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteExpertGroupPictureService implements DeleteExpertGroupPictureUseCase {

    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final LoadExpertGroupPort loadExpertGroupPort;
    private final DeleteFilePort deleteFilePort;
    private final UpdateExpertGroupPicturePort updateExpertGroupPicturePort;

    @Override
    public void delete(Param param) {
        validateCurrentUser(param.getExpertGroupId(), param.getCurrentUserId());
        var picture = loadExpertGroupPort.loadExpertGroup(param.getExpertGroupId()).getPicture();

        if (picture != null && !picture.isBlank()) {
            updateExpertGroupPicturePort.updatePicture(param.getExpertGroupId(), null);
            deleteFilePort.deletePicture(picture);
        }
    }

    private void validateCurrentUser(Long expertGroupId, UUID currentUserId) {
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId);
        if (!Objects.equals(expertGroupOwnerId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
