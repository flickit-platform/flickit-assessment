package org.flickit.assessment.users.application.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.FileProperties;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.in.user.UpdateUserProfilePictureUseCase;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.users.application.port.out.minio.DeleteFilePort;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
import org.flickit.assessment.users.application.port.out.user.UpdateUserPicturePort;
import org.flickit.assessment.users.application.port.out.user.UploadUserProfilePicturePort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

import static org.flickit.assessment.common.error.ErrorMessageKey.UPLOAD_FILE_FORMAT_NOT_VALID;
import static org.flickit.assessment.common.error.ErrorMessageKey.UPLOAD_FILE_PICTURE_SIZE_MAX;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateUserProfilePictureService implements UpdateUserProfilePictureUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final FileProperties fileProperties;
    private final LoadUserPort loadUserPort;
    private final DeleteFilePort deleteFilePort;
    private final UploadUserProfilePicturePort uploadUserProfilePicturePort;
    private final UpdateUserPicturePort updateUserPicturePort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Override
    public Result update(Param param) {
        validatePicture(param.getPicture());
        var user = loadUserPort.loadUser(param.getCurrentUserId());

        if (user.getPicturePath() != null && !user.getPicturePath().isEmpty())
            deleteFilePort.deletePicture(user.getPicturePath());

        String filePath = uploadUserProfilePicturePort.uploadUserProfilePicture(param.getPicture());

        var pictureLink = createFileDownloadLinkPort.createDownloadLink(filePath, EXPIRY_DURATION);
        updateUserPicturePort.updatePicture(param.getCurrentUserId(), filePath);

        return new Result(pictureLink);
    }

    private void validatePicture(MultipartFile picture) {
        if (picture.getSize() > fileProperties.getPictureMaxSize().toBytes())
            throw new ValidationException(UPLOAD_FILE_PICTURE_SIZE_MAX);

        if (!fileProperties.getPictureContentTypes().contains(picture.getContentType()))
            throw new ValidationException(UPLOAD_FILE_FORMAT_NOT_VALID);
    }
}
