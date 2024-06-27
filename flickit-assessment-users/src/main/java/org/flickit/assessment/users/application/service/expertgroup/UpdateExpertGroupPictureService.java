package org.flickit.assessment.users.application.service.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.port.in.expertgroup.UpdateExpertGroupPictureUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupPort;
import org.flickit.assessment.users.application.port.out.expertgroup.UpdateExpertGroupPicturePort;
import org.flickit.assessment.users.application.port.out.expertgroup.UploadExpertGroupPicturePort;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.users.application.port.out.minio.DeleteFilePort;
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

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final LoadExpertGroupPort loadExpertGroupPort;
    private final DeleteFilePort deleteFilePort;
    private final UploadExpertGroupPicturePort uploadExpertGroupPicturePort;
    private final UpdateExpertGroupPicturePort updateExpertGroupPicturePort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Override
    public Result update(Param param) {
        validateCurrentUser(param.getExpertGroupId(), param.getCurrentUserId());

        var picturePath = loadExpertGroupPort.loadExpertGroup(param.getExpertGroupId()).getPicture();
        if (picturePath != null && !picturePath.isBlank())
            deleteFilePort.deletePicture(picturePath);

        picturePath = uploadExpertGroupPicturePort.uploadPicture(param.getPicture());
        updateExpertGroupPicturePort.updatePicture(param.getExpertGroupId(), picturePath);

        var pictureLink = createFileDownloadLinkPort.createDownloadLink(picturePath, EXPIRY_DURATION);
        return new Result(pictureLink);
    }

    private void validateCurrentUser(Long expertGroupId, UUID currentUserId) {
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId);
        if (!Objects.equals(expertGroupOwnerId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
