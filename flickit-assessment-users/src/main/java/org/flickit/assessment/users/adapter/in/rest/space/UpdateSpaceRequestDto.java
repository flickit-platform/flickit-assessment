package org.flickit.assessment.users.adapter.in.rest.space;

import lombok.Builder;

@Builder
public record UpdateSpaceRequestDto(String title) {
}
