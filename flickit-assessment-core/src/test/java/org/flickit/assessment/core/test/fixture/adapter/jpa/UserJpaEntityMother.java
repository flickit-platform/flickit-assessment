package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserJpaEntityMother {

    public static UserJpaEntity user() {
        UUID id = UUID.randomUUID();
        return new UserJpaEntity(
            id,
            "email" + id + "@mail.com",
            "name" + id,
            "bio",
            LocalDateTime.now(),
            Boolean.FALSE,
            Boolean.TRUE,
            Boolean.TRUE,
            1L,
            1L
        );
    }
}
