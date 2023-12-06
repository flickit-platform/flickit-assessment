package org.flickit.assessment.kit.application.port.out.subjectquestionnaire;

public interface CreateSubjectQuestionnairePort {

    long persist(Long subjectId, Long questionnaireId);
}
