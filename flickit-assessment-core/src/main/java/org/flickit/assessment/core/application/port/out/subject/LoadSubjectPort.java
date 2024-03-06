package org.flickit.assessment.core.application.port.out.subject;

import org.flickit.assessment.core.application.domain.Subject;

import java.util.Optional;

public interface LoadSubjectPort {

    Optional<Subject> loadByIdAndKitVersionId(long id, long kitVersionId);
}
