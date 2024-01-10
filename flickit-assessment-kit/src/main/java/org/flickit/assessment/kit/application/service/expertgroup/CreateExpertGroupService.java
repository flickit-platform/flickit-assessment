package org.flickit.assessment.kit.application.service.expertgroup;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.expertgroup.CreateExpertGroupUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.CreateExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CreateExpertGroupAccessPort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateExpertGroupService implements CreateExpertGroupUseCase {

    private final CreateExpertGroupPort createExpertGroupPort;
    private final CreateExpertGroupAccessPort createExpertGroupAccessPort;
    @Override
    @Transactional
    public Result createExpertGroup(Param param) {
        CreateExpertGroupPort.Param expertGroupPortParam = toExpertGroupParam(param);
        long idExpertGroup = createExpertGroupPort.persist(expertGroupPortParam);
        CreateExpertGroupAccessPort.Param expertGroupAccessPortParam = toExpertGroupAccessParam(param,idExpertGroup);
        createExpertGroupAccessPort.persist (expertGroupAccessPortParam);
        return new Result(idExpertGroup);
    }

    private CreateExpertGroupPort.Param toExpertGroupParam(Param param) {
        return new CreateExpertGroupPort.Param(
            param.getName(),
            param.getAbout(),
            param.getPicture(),
            param.getWebsite(),
            param.getBio(),
            param.getCurrentUserId()
        );
    }

    private CreateExpertGroupAccessPort.Param toExpertGroupAccessParam(Param param, Long idExpertGroup) {
        return new CreateExpertGroupAccessPort.Param(
            idExpertGroup,
            null,
            null,
            param.getCurrentUserId()
        );
    }
}
