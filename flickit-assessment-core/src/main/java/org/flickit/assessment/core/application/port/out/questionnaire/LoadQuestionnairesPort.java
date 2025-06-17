package org.flickit.assessment.core.application.port.out.questionnaire;

import java.util.Map;
import java.util.UUID;

public interface LoadQuestionnairesPort {

    Map<Long, Result> loadQuestionnaireDetails(long kitVersionId, UUID assessmentResultId);

    record Result(long id, int index, String title, int questionCount, int answerCount) {
    }
}
