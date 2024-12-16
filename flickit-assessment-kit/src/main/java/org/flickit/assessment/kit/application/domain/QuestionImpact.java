package org.flickit.assessment.kit.application.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class QuestionImpact {

    private final Long id;
    private final long attributeId;
    private final long maturityLevelId;
    private final int weight;
    private final Long kitVersionId;
    private final Long questionId;
    @EqualsAndHashCode.Exclude private final LocalDateTime creationTime;
    @EqualsAndHashCode.Exclude private final LocalDateTime lastModificationTime;
    private final UUID createdBy;
    private final UUID lastModifiedBy;
}
