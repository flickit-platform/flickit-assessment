package org.flickit.assessment.core.adapter.out.persistence.maturitylevel;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.kit.domain.Level;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaturityLevelMapper {

    public static Level mapToKitDomainModel(MaturityLevelJpaEntity entity) {
        return new Level(
            "", //TODO
            entity.getTitle(),
            "", //TODO
            0, //TODO
            null, //TODO
            entity.getValue()
        );
    }
}
