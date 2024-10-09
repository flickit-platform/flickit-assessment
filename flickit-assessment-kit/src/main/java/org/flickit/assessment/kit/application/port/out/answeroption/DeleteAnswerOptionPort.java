package org.flickit.assessment.kit.application.port.out.answeroption;

public interface DeleteAnswerOptionPort {

    void deleteByIdAndKitVersionId(Long answerOptionId, Long kitVersionId);
}
