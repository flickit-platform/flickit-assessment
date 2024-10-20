package org.flickit.assessment.kit.application.port.out.subject;

public interface CountSubjectQuestionsPort {

    int countBySubjectId(long subjectId, long kitVersionId);
}
