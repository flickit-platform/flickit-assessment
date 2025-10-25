package org.flickit.assessment.kit.application.port.out.question;

public interface DeleteQuestionPort {

    void delete(long questionId, long kitVersionId);
}
