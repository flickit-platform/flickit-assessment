package org.flickit.assessment.core.application.port.out.answer;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface CountAnswersPort {

    /**
     * Counts the number of answers associated with the specified questions
     * in a given assessment result.
     *
     * @param assessmentResultId the unique identifier of the assessment result
     * @param questionIds        a list of question IDs for which the answers are counted
     * @return the count of answers for the given question IDs in the assessment result
     */
    int countByQuestionIds(UUID assessmentResultId, List<Long> questionIds);

    /**
     * Counts the number of unapproved answers for a given assessment result.
     *
     * @param assessmentResultId the unique identifier of the assessment result
     * @return the count of unapproved answers in the assessment result
     */
    int countUnapprovedAnswers(UUID assessmentResultId);

    /**
     * Counts the number of unapproved answers for each questionnaire
     * in a given assessment result.
     *
     * @param assessmentResultId the unique identifier of the assessment result
     * @param questionnaireIds   a set of questionnaire IDs for which the unapproved answers are counted
     * @return a map where the key is the questionnaire ID and the value is the count of unapproved answers
     */
    Map<Long, Integer> countQuestionnaireUnapprovedAnswers(UUID assessmentResultId, Set<Long> questionnaireIds);
}
