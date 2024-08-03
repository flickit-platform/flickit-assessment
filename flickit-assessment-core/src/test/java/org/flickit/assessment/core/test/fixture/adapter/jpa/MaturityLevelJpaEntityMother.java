package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class MaturityLevelJpaEntityMother {

    public static MaturityLevelJpaEntity mapToJpaEntity(MaturityLevel maturityLevel, Long kitVersionId) {
        return new MaturityLevelJpaEntity(
            maturityLevel.getId(),
            kitVersionId,
            maturityLevel.getTitle().toUpperCase(),
            maturityLevel.getIndex(),
            maturityLevel.getTitle(),
            "description" + maturityLevel.getId(),
            maturityLevel.getValue(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            UUID.randomUUID()
        );
    }
}
