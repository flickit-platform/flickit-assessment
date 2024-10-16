package org.flickit.assessment.kit.application.port.out.subject;

public interface DeleteSubjectPort {

    void delete(long subjectId, long kitVersionId);
}
