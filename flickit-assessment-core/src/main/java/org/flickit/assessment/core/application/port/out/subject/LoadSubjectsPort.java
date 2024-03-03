package org.flickit.assessment.core.application.port.out.subject;

import org.flickit.assessment.core.application.domain.Subject;

import java.util.List;

public interface LoadSubjectsPort {

    List<Subject> loadByKitIdWithAttributes(Long kitId);
}
