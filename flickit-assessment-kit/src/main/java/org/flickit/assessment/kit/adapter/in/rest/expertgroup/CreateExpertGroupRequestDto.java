package org.flickit.assessment.kit.adapter.in.rest.expertgroup;

public record CreateExpertGroupRequestDto(String title,
                                          String bio,
                                          String about,
                                          String website,
                                          String picture) {
}
