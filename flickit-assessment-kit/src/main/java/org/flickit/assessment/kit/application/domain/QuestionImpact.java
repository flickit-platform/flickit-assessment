package org.flickit.assessment.kit.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class QuestionImpact {

    private final Long id;
    private final long attributeId;
    private final long maturityLevelId;
    private final int weight;
    private final Long questionId;
    private final LocalDateTime creationTime;
    private final LocalDateTime lastModificationTime;
    private final UUID createdBy;
    private final UUID lastModifiedBy;
    @Setter
    private List<AnswerOptionImpact> optionImpacts;
}
