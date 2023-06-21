package org.flickit.flickitassessmentcore.application.port.out.qualityattribute;

import java.util.List;

public interface LoadQualityAttributeIdsByAssessmentSubjectPort {

    List<Long> loadIdsByAssessmentSubjectId(Long assessmentSubjectId);
}
