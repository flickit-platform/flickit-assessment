package org.flickit.assessment.users.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Space {

    private final Long id;

    private final String code;

    private final String title;

    private final UUID ownerId;
}
