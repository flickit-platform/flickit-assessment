package org.flickit.assessment.kit.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ExpertGroup {

    private final long id;
    private final String title;
    private final String picture;
    private final UUID ownerId;
}
