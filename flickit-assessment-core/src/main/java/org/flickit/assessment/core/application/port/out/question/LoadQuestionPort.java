package org.flickit.assessment.core.application.port.out.question;

import java.util.List;
import java.util.UUID;

public interface LoadQuestionPort {

    int loadFirstUnansweredQuestionIndex(long questionnaireId, UUID assessmentResultId);

    List<IdAndAnswerRange> loadIdAndAnswerRangeIdByKitVersionId(long kitVersionId);

    record IdAndAnswerRange(long id, long answerRangeId) {
    }
}
