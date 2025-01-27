package org.flickit.assessment.data.jpa.core.assessment;

import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;

public interface AssessmentJoinResultView {

    AssessmentJpaEntity getAssessment();

    AssessmentResultJpaEntity getAssessmentResult();

    boolean getManageable();

    boolean getHasReport();
}
