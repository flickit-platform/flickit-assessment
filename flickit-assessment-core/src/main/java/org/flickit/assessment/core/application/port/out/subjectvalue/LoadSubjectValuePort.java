package org.flickit.assessment.core.application.port.out.subjectvalue;

import org.flickit.assessment.core.application.domain.SubjectValue;

import java.util.UUID;

public interface LoadSubjectValuePort {

    SubjectValue load(long subjectId, UUID assessmentResultId);
}
