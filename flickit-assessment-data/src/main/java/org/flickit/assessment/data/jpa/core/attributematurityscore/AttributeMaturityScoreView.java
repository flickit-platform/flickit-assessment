package org.flickit.assessment.data.jpa.core.attributematurityscore;

import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;

public interface AttributeMaturityScoreView {

    Double getScore();

    long getAttributeId();

    MaturityLevelJpaEntity getMaturityLevel();
}
