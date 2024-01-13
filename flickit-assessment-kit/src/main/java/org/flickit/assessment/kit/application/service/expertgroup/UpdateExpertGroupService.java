package org.flickit.assessment.kit.application.service.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.expertgroup.UpdateExpertGroupUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.UpdateExpertGroupPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateExpertGroupService implements UpdateExpertGroupUseCase {

    private  final UpdateExpertGroupPort updateExpertGroupPort;
    @Override
    public void updateExpertGroup(Param param) {
        updateExpertGroupPort.update(toExpertGroupParam(param));
    }

    private UpdateExpertGroupPort.Param toExpertGroupParam(Param param) {
        return new UpdateExpertGroupPort.Param(
            param.getId(),
            param.getName(),
            param.getAbout(),
            param.getPicture(),
            param.getWebsite(),
            param.getBio(),
            param.getCurrentUserId()
        );
    }
}
