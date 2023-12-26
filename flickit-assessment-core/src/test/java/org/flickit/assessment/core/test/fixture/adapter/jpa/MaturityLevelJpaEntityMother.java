package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;

public class MaturityLevelJpaEntityMother {

    public static int index = 1;

    public static MaturityLevelJpaEntity maturityLevelEntity(Long id) {
        return new MaturityLevelJpaEntity(
            id,
            "code" + id,
            "title" + id,
            id.intValue(),
            index++,
            1L
        );
    }
}
