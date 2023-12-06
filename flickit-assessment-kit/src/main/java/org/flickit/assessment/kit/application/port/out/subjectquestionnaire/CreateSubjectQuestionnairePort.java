package org.flickit.assessment.kit.application.port.out.subjectquestionnaire;

public interface CreateSubjectQuestionnairePort {

    long persist(long subjectId, long questionnaireId);
}
