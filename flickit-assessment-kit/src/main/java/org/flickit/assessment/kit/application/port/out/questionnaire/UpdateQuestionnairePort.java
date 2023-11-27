package org.flickit.assessment.kit.application.port.out.questionnaire;

public interface UpdateQuestionnairePort {

    void update(Param param);

    record Param(Long id, String title, Integer index, String description) {
    }
}
