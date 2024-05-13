package org.flickit.assessment.users.application.service.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.in.expertgroup.UpdateExpertGroupPictureUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.*;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.UPDATE_EXPERT_GROUP_PICTURE_PICTURE_NOT_NULL;

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
        if ((Objects.requireNonNull(param.getPicture().getOriginalFilename())).isEmpty())
            throw new ValidationException(UPDATE_EXPERT_GROUP_PICTURE_PICTURE_NOT_NULL);

        validateCurrentUser(param.getExpertGroupId(), param.getCurrentUserId());
        var picture = loadExpertGroupPort.loadExpertGroup(param.getExpertGroupId()).getPicture();

        if (picture == null || picture.isBlank()) {
            picture = uploadExpertGroupPicturePort.uploadPicture(param.getPicture());
            updateExpertGroupPicturePort.updatePicture(param.getExpertGroupId(), picture);
        } else
            updateExpertGroupPictureFilePort.updatePicture(param.getPicture(), picture);

        var pictureLink = createFileDownloadLinkPort.createDownloadLink(picture, EXPIRY_DURATION);
        return new Result(pictureLink);
    }

    private void validateCurrentUser(Long expertGroupId, UUID currentUserId) {
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId);
        if (!Objects.equals(expertGroupOwnerId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
