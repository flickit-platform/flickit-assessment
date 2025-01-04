package org.flickit.assessment.core.application.port.out.evidence;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CountEvidencesPort {

    /**
     * Counts the number of answered questions that have evidence
     * for a given assessment.
     *
     * @param assessmentId the unique identifier of the assessment
     * @return the count of answered questions having evidence
     */
    int countAnsweredQuestionsHavingEvidence(UUID assessmentId);

    /**
     * Counts the number of unresolved comments
     * for a given assessment.
     *
     * @param assessmentId the unique identifier of the assessment
     * @return the count of unresolved comments of an assessment
     */
    int countUnresolvedComments(UUID assessmentId);

    /**
     * Counts the number of answered questions that have evidence
     * for a list the given questionnaires,
     *
     * @param assessmentId the unique identifier of the assessment
     * @param kitVersionId the identifier of the kit version
     * @param questionnaireIds a list of questionnaire IDs to filter the questions
     * @return a map where the key is the questionnaire ID and the value is the count of answered questions having evidence
     */
    Map<Long, Integer> countAnsweredQuestionsHavingEvidence(UUID assessmentId, long kitVersionId, List<Long> questionnaireIds);

    /**
     * Counts the number of unresolved comments
     * for a given list of questionnaire IDs.
     *
     * @param assessmentId the unique identifier of the assessment
     * @param kitVersionId the identifier of the kit version
     * @param questionnaireIds a list of questionnaire IDs to filter the comments
     * @return a map where the key is the questionnaire ID and the value is the count of unresolved comments
     */
    Map<Long, Integer> countUnresolvedComments(UUID assessmentId, long kitVersionId, List<Long> questionnaireIds);
}
