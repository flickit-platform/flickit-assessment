package org.flickit.assessment.core.application.port.out.questionnaire;

import java.util.List;
import java.util.UUID;

public interface LoadQuestionnairesPort {

    List<Result> loadQuestionnaireDetails(long kitVersionId, UUID assessmentResultId);

    record Result(long id, int index, String title, int questionCount, int answerCount) {
    }
}
