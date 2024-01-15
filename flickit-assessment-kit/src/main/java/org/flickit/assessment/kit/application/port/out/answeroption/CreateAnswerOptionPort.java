package org.flickit.assessment.kit.application.port.out.answeroption;

public interface CreateAnswerOptionPort {

    Long persist(Param param);

    record Param(String title, Long questionId, Integer index, java.util.UUID currentUserId) {}
}
