package org.flickit.assessment.users.adapter.in.rest.space;

import java.time.LocalDateTime;

public record GetSpaceResponseDto(long id,
                                  String code,
                                  String title,
                                  SpaceTypeDto type,
                                  boolean editable,
                                  LocalDateTime lastModificationTime,
                                  int membersCount,
                                  int assessmentsCount,
                                  boolean canCreateAssessment) {

    public record SpaceTypeDto(String code, String title) {
    }
}
