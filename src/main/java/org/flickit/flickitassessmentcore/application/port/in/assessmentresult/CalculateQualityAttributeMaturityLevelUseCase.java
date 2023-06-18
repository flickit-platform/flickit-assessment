package org.flickit.flickitassessmentcore.application.port.in.assessmentresult;

import org.flickit.flickitassessmentcore.domain.AssessmentResult;
import org.flickit.flickitassessmentcore.domain.QualityAttribute;
import org.flickit.flickitassessmentcore.domain.QualityAttributeValue;

public interface CalculateQualityAttributeMaturityLevelUseCase {

    public QualityAttributeValue calculateQualityAttributeMaturityLevel(AssessmentResult assessmentResult, QualityAttribute qualityAttribute);
}
