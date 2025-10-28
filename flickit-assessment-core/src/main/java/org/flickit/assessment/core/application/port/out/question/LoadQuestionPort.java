package org.flickit.assessment.core.application.port.out.question;

import org.flickit.assessment.core.application.domain.Question;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface LoadQuestionPort {

    Question loadByIdAndKitVersionId(Long id, Long kitVersionId);

    int loadFirstUnansweredQuestionIndex(long questionnaireId, UUID assessmentResultId);

    List<IdAndAnswerRange> loadIdAndAnswerRangeIdByKitVersionId(long kitVersionId);

    record IdAndAnswerRange(long id, long answerRangeId) {
    }
}
