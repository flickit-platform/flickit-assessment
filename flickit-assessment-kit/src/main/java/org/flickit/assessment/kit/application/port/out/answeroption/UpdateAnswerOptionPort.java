package org.flickit.assessment.kit.application.port.out.answeroption;

public interface UpdateAnswerOptionPort {

    void update(Param param);

    record Param(Long id, String title) {}
}
