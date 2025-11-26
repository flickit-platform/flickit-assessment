package org.flickit.assessment.core.application.port.out.question;

import org.flickit.assessment.core.application.domain.Question;

import java.util.List;
import java.util.UUID;

public interface LoadQuestionPort {

    int loadFirstUnansweredQuestionIndex(long questionnaireId, UUID assessmentResultId);

    Question loadQuestion(long questionId, long kitVersionId, int langId);

    List<IdAndAnswerRange> loadIdAndAnswerRangeIdByKitVersionId(long kitVersionId);

    record IdAndAnswerRange(long id, long answerRangeId) {
    }
}
