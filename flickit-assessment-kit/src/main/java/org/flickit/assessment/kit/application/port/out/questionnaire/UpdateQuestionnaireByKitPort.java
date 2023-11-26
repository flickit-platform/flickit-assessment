package org.flickit.assessment.kit.application.port.out.questionnaire;

public interface UpdateQuestionnaireByKitPort {

    void updateByKitId(Param param);

    record Param(String code, String title, String description, Integer index, Long kitId) {
    }
}
