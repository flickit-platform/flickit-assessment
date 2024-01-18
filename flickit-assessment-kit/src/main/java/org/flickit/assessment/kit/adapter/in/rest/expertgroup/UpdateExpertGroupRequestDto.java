package org.flickit.assessment.kit.adapter.in.rest.expertgroup;

import org.springframework.web.multipart.MultipartFile;

public record UpdateExpertGroupRequestDto(String title,
                                          String bio,
                                          String about,
                                          MultipartFile picture,
                                          String website) {
}
