package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.users.application.port.in.space.GetSpaceTypesUseCase;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GetSpaceTypesService implements GetSpaceTypesUseCase {

    @Override
    public Result getSpaceTypes() {
        List<Result.SpaceType> spaceTypes = Arrays.stream(SpaceType.values())
            .map(e -> new Result.SpaceType(e.getCode(), e.getTitle()))
            .toList();
        return new Result(spaceTypes);
    }
}
