package org.flickit.assessment.users.adapter.in.rest.expertgroup;

import lombok.Builder;

@Builder
public record UpdateExpertGroupRequestDto(String title,
                                          String bio,
                                          String about,
                                          String website) {
}
