package org.flickit.assessment.users.adapter.in.rest.expertgroup;

public record UpdateExpertGroupRequestDto(String title,
                                          String bio,
                                          String about,
                                          String website) {
}
