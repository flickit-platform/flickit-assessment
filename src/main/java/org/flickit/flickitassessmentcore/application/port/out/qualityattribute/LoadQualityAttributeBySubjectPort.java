package org.flickit.flickitassessmentcore.application.port.out.qualityattribute;

import org.flickit.flickitassessmentcore.domain.QualityAttribute;

import java.util.List;

public interface LoadQualityAttributeBySubjectPort {

    Result loadQualityAttributeBySubjectId(Long subjectId);

    record Result(List<QualityAttribute> qualityAttribute) {}
}
