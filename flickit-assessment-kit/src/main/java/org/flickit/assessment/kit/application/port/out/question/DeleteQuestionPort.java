package org.flickit.assessment.kit.application.port.out.question;

public interface DeleteQuestionPort {

    void deleteQuestion(long questionId, long kitVersionId);
}
