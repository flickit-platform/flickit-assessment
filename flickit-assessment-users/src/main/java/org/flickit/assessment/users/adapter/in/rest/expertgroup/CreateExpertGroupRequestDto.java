package org.flickit.assessment.users.adapter.in.rest.expertgroup;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;
import org.springframework.web.multipart.MultipartFile;

@Builder
@FieldNameConstants
public record CreateExpertGroupRequestDto(String title,
                                          String bio,
                                          String about,
                                          MultipartFile picture,
                                          String website) {
}
