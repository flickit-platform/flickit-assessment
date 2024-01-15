package org.flickit.assessment.kit.application.service.expertgroup;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.expertgroup.CreateExpertGroupUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.CreateExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CreateExpertGroupAccessPort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateExpertGroupService implements CreateExpertGroupUseCase {

    private final CreateExpertGroupPort createExpertGroupPort;
    private final CreateExpertGroupAccessPort createExpertGroupAccessPort;

    @Override
    public Result createExpertGroup(Param param) {
        long expertGroupId = createExpertGroupPort.persist(toExpertGroupParam(param));
        CreateExpertGroupAccessPort.Param expertGroupAccessPortParam = toExpertGroupAccessParam(param, expertGroupId);
        createExpertGroupAccessPort.persist(expertGroupAccessPortParam);
        return new Result(expertGroupId);
    }

    private CreateExpertGroupPort.Param toExpertGroupParam(Param param) {
        return new CreateExpertGroupPort.Param(
            param.getTitle(),
            param.getAbout(),
            param.getPicture(),
            param.getWebsite(),
            param.getBio(),
            param.getCurrentUserId()
        );
    }

    private CreateExpertGroupAccessPort.Param toExpertGroupAccessParam(Param param, Long expertGroupId) {
        return new CreateExpertGroupAccessPort.Param(
            expertGroupId,
            null,
            null,
            param.getCurrentUserId()
        );
    }
}
