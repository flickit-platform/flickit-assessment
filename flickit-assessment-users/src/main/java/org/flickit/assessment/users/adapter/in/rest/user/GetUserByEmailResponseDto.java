package org.flickit.assessment.users.adapter.in.rest.user;

import java.util.UUID;

public record GetUserByEmailResponseDto(UUID id,
                                        String email,
                                        String displayName,
                                        String bio,
                                        String linkedin,
                                        String picture) {
}
