package org.flickit.assessment.kit.application.port.out.subject;

import org.flickit.assessment.kit.application.domain.Subject;

public interface CreateSubjectPort {

    Long persist(Subject subject, Long kitId);
}
