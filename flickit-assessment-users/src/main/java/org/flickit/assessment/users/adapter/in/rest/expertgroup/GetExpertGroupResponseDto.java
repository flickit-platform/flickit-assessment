package org.flickit.assessment.users.adapter.in.rest.expertgroup;

public record GetExpertGroupResponseDto(long id,
                                        String title,
                                        String bio,
                                        String about,
                                        String pictureLink,
                                        String website,
                                        boolean editable) {
}
