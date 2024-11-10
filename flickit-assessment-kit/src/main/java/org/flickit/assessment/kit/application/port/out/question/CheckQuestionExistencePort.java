package org.flickit.assessment.kit.application.port.out.question;

public interface CheckQuestionExistencePort {

    boolean checkByAnswerRangeIdAndKitVersionId(long answerRangeId, long kitVersionId);
}
