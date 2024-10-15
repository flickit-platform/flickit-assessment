package org.flickit.assessment.advice.adapter.out.persistence.maturitylevel;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.advice.application.domain.MaturityLevel;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaturityLevelMapper {

    public static MaturityLevel mapToDomainModel(MaturityLevelJpaEntity entity) {
        return new MaturityLevel(
            entity.getId(),
            entity.getTitle());
    }
}
