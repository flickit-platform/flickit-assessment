package org.flickit.assessment.kit.application.service.expertgroup;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.expertgroup.CreateExpertGroupUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.CreateExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.UploadExpertGroupPicturePort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CreateExpertGroupAccessPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateExpertGroupService implements CreateExpertGroupUseCase {

    private final CreateExpertGroupPort createExpertGroupPort;
    private final CreateExpertGroupAccessPort createExpertGroupAccessPort;
    private final UploadExpertGroupPicturePort uploadExpertGroupPicturePort;

    @Override
    public Result createExpertGroup(Param param) {
        var uploadResult = uploadExpertGroupPicturePort.upload(null);
        long expertGroupId = createExpertGroupPort.persist(toCreateExpertGroupParam(param,uploadResult.pictureFilePath()));

        createOwnerAccessToGroup(expertGroupId, param.getCurrentUserId());

        return new Result(expertGroupId);
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

    private CreateExpertGroupPort.Param toCreateExpertGroupParam(Param param, String pictureFilePath) {
        return new CreateExpertGroupPort.Param(
            param.getTitle(),
            param.getAbout(),
            pictureFilePath,
            param.getWebsite(),
            param.getBio(),
            param.getCurrentUserId()
        );
    }
}
