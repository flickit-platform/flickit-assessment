package org.flickit.assessment.kit.application.port.out.subject;

import org.flickit.assessment.kit.application.domain.Subject;

public interface LoadSubjectPort {

    Subject load(long subjectId, long kitVersionId);
}
