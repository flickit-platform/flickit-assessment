package org.flickit.assessment.core.application.port.out.subjectvalue;

import org.flickit.assessment.core.application.domain.SubjectValue;

import java.util.List;
import java.util.UUID;

public interface LoadSubjectValuesPort {

    List<SubjectValue> loadByAssessmentId(UUID assessmentId);
}
