package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.users.application.port.in.space.GetSpaceTypesUseCase.Result;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GetSpaceTypesServiceTest {

    private final GetSpaceTypesService service = new GetSpaceTypesService();

    @Test
    void getSpaceTypes() {
        List<Result.SpaceType> spaceTypes = Arrays.stream(SpaceType.values())
            .map(e -> new Result.SpaceType(e.getCode(), e.getTitle()))
            .toList();

        assertEquals(new Result(spaceTypes), service.getSpaceTypes());
    }
}
