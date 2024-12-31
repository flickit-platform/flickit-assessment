package org.flickit.assessment.core.application.port.out.questionnaire;

import java.util.List;
import java.util.UUID;

public interface GetQuestionnairesProgressPort {

    List<Result> getQuestionnairesProgress(UUID assessmentId, long[] ids);

    record Result(long id, int answersCount, int questionsCount) {
    }
}
