package org.flickit.assessment.kit.adapter.out.persistence.maturitylevel;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.kit.application.domain.MaturityLevel;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaturityLevelMapper {

    public static MaturityLevel mapToDomainModel(MaturityLevelJpaEntity entity) {
        return new MaturityLevel(
            entity.getId(),
            entity.getCode(),
            entity.getTitle(),
            entity.getIndex(),
            entity.getDescription(),
            entity.getValue()
        );
    }

    public static MaturityLevelJpaEntity mapToJpaEntityToPersist(MaturityLevel level, Long kitVersionId, UUID createdBy) {
        LocalDateTime creationTime = LocalDateTime.now();
        return new MaturityLevelJpaEntity(
            null,
            kitVersionId,
            level.getCode(),
            level.getIndex(),
            level.getTitle(),
            level.getDescription(),
            level.getValue(),
            creationTime,
            creationTime,
            createdBy,
            createdBy
        );
    }
}
