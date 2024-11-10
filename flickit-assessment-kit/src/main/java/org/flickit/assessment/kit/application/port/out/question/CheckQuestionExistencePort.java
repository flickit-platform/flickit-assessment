package org.flickit.assessment.kit.application.port.out.question;

public interface CheckQuestionExistencePort {

    boolean checkByAnswerRange(long answerRangeId, long kitVersionId);
}
