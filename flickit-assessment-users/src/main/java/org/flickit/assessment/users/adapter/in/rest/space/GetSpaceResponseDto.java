package org.flickit.assessment.users.adapter.in.rest.space;

import java.time.LocalDateTime;

public record GetSpaceResponseDto(long id,
                                  String code,
                                  String title,
                                  boolean editable,
                                  LocalDateTime lastModificationTime,
                                  int membersCount,
                                  int assessmentsCount) {
}
