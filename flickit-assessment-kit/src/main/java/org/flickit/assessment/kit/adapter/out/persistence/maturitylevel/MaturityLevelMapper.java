package org.flickit.assessment.kit.adapter.out.persistence.maturitylevel;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.kit.application.domain.Level;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaturityLevelMapper {

    public static Level mapToKitDomainModel(MaturityLevelJpaEntity entity) {
        return new Level(
            entity.getId(),
            entity.getTitle(),
            entity.getTitle(),
            null,
            entity.getValue()
        );
    }
}
