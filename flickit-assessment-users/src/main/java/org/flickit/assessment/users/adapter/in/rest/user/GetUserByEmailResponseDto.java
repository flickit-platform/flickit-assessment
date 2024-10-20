package org.flickit.assessment.users.adapter.in.rest.user;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetUserByEmailResponseDto(UUID id,
                                        String email,
                                        String displayName,
                                        String bio,
                                        String linkedin,
                                        String picture,
                                        LocalDateTime lastLogin,
                                        boolean isSuperUser,
                                        boolean isStaff,
                                        boolean isActive,
                                        String password) {
}
