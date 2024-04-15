package org.flickit.assessment.kit.application.port.out.subject;

import org.flickit.assessment.kit.application.domain.Subject;

import java.util.List;

public interface LoadSubjectsPort {

    List<Subject> loadSubjectsOrderByIndex(long kitVersionId);
}
