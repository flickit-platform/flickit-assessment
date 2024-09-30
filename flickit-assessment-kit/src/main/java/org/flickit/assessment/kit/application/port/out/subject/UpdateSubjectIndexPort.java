package org.flickit.assessment.kit.application.port.out.subject;

public interface UpdateSubjectIndexPort {

    void updateIndex(long kitVersionId, long subjectId, int index);
}
