package org.flickit.assessment.kit.application.port.out.subjectquestionnaire;

public interface CreateSubjectQuestionnairePort {

    void persist(Param param);

    record Param(
        Long subjectId,
        Long questionnaireId
    ) {
    }
}
