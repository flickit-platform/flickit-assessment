package org.flickit.assessment.kit.application.service.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.expertgroup.CreateExpertGroupUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.CreateExpertGroupPort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateExpertGroupService implements CreateExpertGroupUseCase {

    private final CreateExpertGroupPort createExpertGroupPort;
    @Override
    public Result createExpertGroup(Param param) {
        CreateExpertGroupPort.Param portParam = toParam(param);
        long id = createExpertGroupPort.persist(portParam);
        return new Result(id);
    }

    private CreateExpertGroupPort.Param toParam(Param param) {

        return new CreateExpertGroupPort.Param(
            param.getName(),
            param.getAbout(),
            param.getPicture(),
            param.getWebsite(),
            param.getBio(),
            param.getCurrentUserId()
        );
    }
}
