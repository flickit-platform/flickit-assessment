package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.users.application.port.out.space.CheckCreateSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.CountSpacesPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CheckCreateSpaceService implements CheckCreateSpaceUseCase {

    private final CountSpacesPort countSpacesPort;
    private final AppSpecProperties appSpecProperties;

    @Override
    public Result checkCreateSpace(Param param) {
        var userBasicSpaces = countSpacesPort.countBasicSpaces(param.getCurrentUserId());
        var maxAllowedSpaces = appSpecProperties.getSpace().getMaxBasicSpaces();
        var allowCreateBasic = userBasicSpaces < maxAllowedSpaces;

        return new Result(allowCreateBasic);
    }
}
