package org.flickit.assessment.kit.adapter.in.rest.expertgroup;

public record GetExpertGroupResponseDto(long id,
                                        String title,
                                        String bio,
                                        String about,
                                        String pictureLink,
                                        String website,
                                        boolean editable) {
}
