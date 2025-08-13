package org.flickit.assessment.users.adapter.in.rest.space;

import org.flickit.assessment.users.application.port.in.space.GetTopSpacesUseCase.Result;

import java.util.List;

public record GetTopSpacesResponseDto(List<SpaceListItemDto> items) {

    public record SpaceListItemDto(long id, String title, TypeDto type, boolean selected, boolean isDefault) {

        public record TypeDto(String code, String title) {

            public static TypeDto of(Result.SpaceListItem.Type spaceType) {
                return new TypeDto(spaceType.code(), spaceType.title());
            }
        }
    }
}
