package org.flickit.assessment.users.adapter.in.rest.expertgroup;

import org.springframework.web.multipart.MultipartFile;

public record CreateExpertGroupRequestDto(String title,
                                          String bio,
                                          String about,
                                          MultipartFile picture,
                                          String website) {
}
