package org.flickit.assessment.kit.adapter.in.rest.expertgroup;

import org.springframework.web.multipart.MultipartFile;

public record CreateExpertGroupRequestDto(String title,
                                          String bio,
                                          String about,
                                          String website,
                                          MultipartFile picture) {
}
