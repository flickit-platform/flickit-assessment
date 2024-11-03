package org.flickit.assessment.kit.application.port.out.subjectquestionnaire;

import org.flickit.assessment.kit.application.domain.SubjectQuestionnaire;

import java.util.List;

public interface LoadSubjectQuestionnairePort {

    /**
     * Loads a list of saved{@link SubjectQuestionnaire} based on the provided kit version ID.
     *
     * @param kitVersionId The unique identifier for the kitVersion.
     * @return A list of {@link SubjectQuestionnaire} associated with the given kit version.
     */
    List<SubjectQuestionnaire> loadByKitVersionId(long kitVersionId);

    /**
     * Extracts pairs of {@link SubjectQuestionnaire} based on the provided kit version ID.
     * This method retrieves pairs of questionnaires and subject based on the impacts of questionnaire's questions.
     * Each pair shows that some of the questions of the questionnaire have impact on attributes of the subject.
     * @param kitVersionId The unique identifier for the kitVersion.
     * @return A list of {@link SubjectQuestionnaire} pairs associated with the given kit version.
     */
    List<SubjectQuestionnaire> extractPairs(long kitVersionId);
}
