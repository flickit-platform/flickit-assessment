package org.flickit.assessment.users.application.port.in.space;

import java.util.List;

public interface GetSpaceTypesUseCase {

    Result getSpaceTypes();

    record Result(List<SpaceType> spaceTypes) {

        public record SpaceType(String code, String title) {
        }
    }
}
