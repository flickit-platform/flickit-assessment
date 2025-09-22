package org.flickit.assessment.core.adapter.in.rest.space;

import org.flickit.assessment.core.application.port.in.space.GetAssessmentMoveTargetsUseCase.Result;

import java.util.List;

public record GetAssessmentMoveTargetsResponseDto(List<SpaceListItemDto> items) {

    public record SpaceListItemDto(long id, String title, TypeDto type, boolean selected, boolean isDefault) {

        public record TypeDto(String code, String title) {

            public static TypeDto of(Result.SpaceListItem.Type spaceType) {
                return new TypeDto(spaceType.code(), spaceType.title());
            }
        }
    }
}
