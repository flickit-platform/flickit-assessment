package org.flickit.assessment.data.jpa.kit.maturitylevel;

import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaEntity;

public interface MaturityJoinCompetenceView {

    MaturityLevelJpaEntity getMaturityLevel();

    LevelCompetenceJpaEntity getLevelCompetence();
}
