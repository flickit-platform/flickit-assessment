package org.flickit.assessment.kit.application.port.out.question;

public interface CheckQuestionExistencePort {

    boolean existsByAnswerRange(long answerRangeId, long kitVersionId);
}
