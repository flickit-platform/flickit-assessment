package org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.LevelCompetence;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaturityLevelMapper {

    public static MaturityLevel mapToDomainModel(MaturityLevelJpaEntity entity, List<LevelCompetence> competences) {
        return new MaturityLevel(
            entity.getId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getIndex(),
            entity.getValue(),
            entity.getDescription(),
            competences
        );
    }
}
