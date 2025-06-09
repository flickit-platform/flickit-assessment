package org.flickit.assessment.users.adapter.in.rest.space;

import org.flickit.assessment.users.application.port.in.space.GetTopSpacesUseCase;

public record GetTopSpacesResponseDto(long id, String title, Type type, boolean isDefault) {

    public record Type(String code, String title) {

        public static Type of(GetTopSpacesUseCase.SpaceListItem.Type type) {

            return new Type(type.code(), type.title());
        }
    }
}
