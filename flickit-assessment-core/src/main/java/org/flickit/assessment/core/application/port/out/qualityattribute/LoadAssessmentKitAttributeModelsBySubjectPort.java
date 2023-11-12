package org.flickit.assessment.core.application.port.out.qualityattribute;

import org.flickit.assessment.kit.domain.Attribute;

import java.util.List;

public interface LoadAssessmentKitAttributeModelsBySubjectPort {

    List<Attribute> load(Long subjectId);
}
