package org.flickit.assessment.kit.application.port.out.subject;

import org.flickit.assessment.kit.application.domain.Subject;

import java.util.Optional;

public interface LoadSubjectDetailPort {

    Optional<Subject> loadById(long subjectId);
}
