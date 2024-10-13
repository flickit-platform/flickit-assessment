package org.flickit.assessment.users.application.service.expertgroup;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.config.FileProperties;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.common.util.SlugCodeUtil;
import org.flickit.assessment.users.application.domain.ExpertGroupAccessStatus;
import org.flickit.assessment.users.application.port.in.expertgroup.CreateExpertGroupUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.CreateExpertGroupPort;
import org.flickit.assessment.users.application.port.out.expertgroup.UploadExpertGroupPicturePort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.CreateExpertGroupAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.UPLOAD_FILE_FORMAT_NOT_VALID;
import static org.flickit.assessment.common.error.ErrorMessageKey.UPLOAD_FILE_PICTURE_SIZE_MAX;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CreateExpertGroupService implements CreateExpertGroupUseCase {

    private final FileProperties fileProperties;
    private final CreateExpertGroupPort createExpertGroupPort;
    private final CreateExpertGroupAccessPort createExpertGroupAccessPort;
    private final UploadExpertGroupPicturePort uploadExpertGroupPicturePort;

    @Override
    public Result createExpertGroup(Param param) {
        String pictureFilePath = null;
        if (param.getPicture() != null && !param.getPicture().isEmpty()) {
            validatePicture(param.getPicture());
            pictureFilePath = uploadExpertGroupPicturePort.uploadPicture(param.getPicture());
        }

        long expertGroupId = createExpertGroupPort.persist(toCreateExpertGroupParam(param, pictureFilePath));
        createOwnerAccessToGroup(expertGroupId, param.getCurrentUserId());

        return new Result(expertGroupId);
    }

    private void validatePicture(MultipartFile picture) {
        if (picture.getSize() >= fileProperties.getPictureMaxSize().toBytes())
            throw new ValidationException(UPLOAD_FILE_PICTURE_SIZE_MAX);

        if (!fileProperties.getPictureContentTypes().contains(picture.getContentType()))
            throw new ValidationException(UPLOAD_FILE_FORMAT_NOT_VALID);
    }

    private CreateExpertGroupPort.Param toCreateExpertGroupParam(Param param, String pictureFilePath) {
        return new CreateExpertGroupPort.Param(
            SlugCodeUtil.generateSlugCode(param.getTitle()),
            param.getTitle(),
            param.getBio(),
            param.getAbout(),
            pictureFilePath,
            param.getWebsite(),
            param.getCurrentUserId()
        );
    }

    private void createOwnerAccessToGroup(Long expertGroupId, UUID ownerId) {
        CreateExpertGroupAccessPort.Param param = new CreateExpertGroupAccessPort.Param(
            expertGroupId,
            ownerId,
            ExpertGroupAccessStatus.ACTIVE
        );
        createExpertGroupAccessPort.persist(param);
    }
}
