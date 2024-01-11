package org.flickit.assessment.kit.adapter.in.rest.expertgroup;

public record UpdateExpertGroupRequestDto(String name,
                                          String bio,
                                          String about,
                                          String website,
                                          String picture) {
}
