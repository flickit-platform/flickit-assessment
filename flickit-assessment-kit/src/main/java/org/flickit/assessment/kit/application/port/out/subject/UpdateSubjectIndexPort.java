package org.flickit.assessment.kit.application.port.out.subject;

public interface UpdateSubjectIndexPort {

    void updateIndex(Long kitVersionId, Long subjectId, int index);
}
