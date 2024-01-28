package org.flickit.assessment.kit.application.port.out.answeroption;

public interface DeleteAnswerOptionPort {

    void delete(Long id, Long questionId);
}
