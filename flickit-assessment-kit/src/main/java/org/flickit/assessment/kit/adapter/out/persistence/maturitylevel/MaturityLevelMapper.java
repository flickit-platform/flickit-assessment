package org.flickit.assessment.kit.adapter.out.persistence.maturitylevel;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.kit.application.domain.MaturityLevel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaturityLevelMapper {

    public static MaturityLevel mapToDomainModel(MaturityLevelJpaEntity entity) {
        return new MaturityLevel(
            entity.getId(),
            entity.getCode(),
            entity.getTitle(),
            entity.getIndex(),
            entity.getValue(),
            null
        );
    }

    public static MaturityLevelJpaEntity mapToJpaEntity(MaturityLevel level, Long kitId) {
        return new MaturityLevelJpaEntity(
            null,
            level.getCode(),
            level.getTitle(),
            level.getValue(),
            level.getIndex(),
            kitId
        );
    }
}
