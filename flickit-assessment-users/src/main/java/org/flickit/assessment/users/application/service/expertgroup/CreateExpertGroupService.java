package org.flickit.assessment.users.application.service.expertgroup;

import org.flickit.assessment.users.application.port.in.expertgroup.CreateExpertGroupUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.CreateExpertGroupPort;
import org.flickit.assessment.users.application.port.out.expertgroup.UploadExpertGroupPicturePort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.CreateExpertGroupAccessPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateExpertGroupService implements CreateExpertGroupUseCase {

    private final CreateExpertGroupPort createExpertGroupPort;
    private final CreateExpertGroupAccessPort createExpertGroupAccessPort;
    private final UploadExpertGroupPicturePort uploadExpertGroupPicturePort;

    @Override
    public Result createExpertGroup(Param param) {
        String pictureFilePath = null;

        if (param.getPicture() != null && !param.getPicture().isEmpty())
            pictureFilePath = uploadExpertGroupPicturePort.uploadPicture(param.getPicture());

        long expertGroupId = createExpertGroupPort.persist(toCreateExpertGroupParam(param, pictureFilePath));
        createOwnerAccessToGroup(expertGroupId, param.getCurrentUserId());

        return new Result(expertGroupId);
    }

    private CreateExpertGroupPort.Param toCreateExpertGroupParam(Param param, String pictureFilePath) {
        return new CreateExpertGroupPort.Param(
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
            null,
            null,
            ownerId
        );
        createExpertGroupAccessPort.persist(param);
    }
}
