package org.flickit.assessment.kit.application.port.out.subjectquestionnaire;

public interface DeleteSubjectQuestionnairePort {

    void delete(long id);

    void deleteByKitVersionId(long kitVersionId);
}
