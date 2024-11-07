package org.flickit.assessment.users.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ExpertGroupMember {

    private final Long expertGroupId;
    private final UUID userId;
    private final String displayName;
}
