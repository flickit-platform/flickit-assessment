package org.flickit.assessment.kit.application.port.out.subject;

public interface CheckSubjectExistencePort {

    boolean exist(long kitId, long subjectId);
}
