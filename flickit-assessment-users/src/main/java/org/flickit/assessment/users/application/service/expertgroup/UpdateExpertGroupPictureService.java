package org.flickit.assessment.users.application.service.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.port.in.expertgroup.UpdateExpertGroupPictureUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.*;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateExpertGroupPictureService implements UpdateExpertGroupPictureUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofHours(1);

    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final LoadExpertGroupPort loadExpertGroupPort;
    private final UpdateExpertGroupPictureFilePort updateExpertGroupPictureFilePort;
    private final UpdateExpertGroupPicturePort updateExpertGroupPicturePort;
    private final UploadExpertGroupPicturePort uploadExpertGroupPicturePort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;


    @Override
    public Result update(Param param) {
        validateCurrentUser(param.getExpertGroupId(), param.getCurrentUserId());
        var oldPicture = loadExpertGroupPort.loadExpertGroup(param.getExpertGroupId()).getPicture();
        String picture;
        if (oldPicture != null && !oldPicture.isBlank())
            picture = updateExpertGroupPictureFilePort.updatePicture(param.getPicture(), oldPicture);
        else {
            picture = uploadExpertGroupPicturePort.uploadPicture(param.getPicture());
            updateExpertGroupPicturePort.updatePicture(param.getExpertGroupId(), picture);
        }

        var pictureLink = createFileDownloadLinkPort.createDownloadLink(picture, EXPIRY_DURATION);
        return new Result(pictureLink);
    }

    private void validateCurrentUser(Long expertGroupId, UUID currentUserId) {
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId);
        if (!Objects.equals(expertGroupOwnerId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
