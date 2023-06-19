package org.flickit.assessment.core.application.port.in.assessmentresult;

import org.flickit.assessment.core.domain.AssessmentResult;
import org.flickit.assessment.core.domain.QualityAttribute;
import org.flickit.assessment.core.domain.QualityAttributeValue;

public interface CalculateQualityAttributeMaturityLevelUseCase {

    public QualityAttributeValue calculateQualityAttributeMaturityLevel(AssessmentResult assessmentResult, QualityAttribute qualityAttribute);
}
