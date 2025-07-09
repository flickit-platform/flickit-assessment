package org.flickit.assessment.users.adapter.in.rest.space;

import org.flickit.assessment.users.application.port.in.space.GetTopSpacesUseCase.Result;

import java.util.List;

public record GetTopSpacesResponseDto(List<SpaceListItem> items) {

    public record SpaceListItem(long id, String title, Type type, boolean isDefault) {

        public record Type(String code, String title) {

            public static Type of(Result.SpaceListItem.Type spaceType) {
                return new Type(spaceType.code(), spaceType.title());
            }
        }
    }
}
