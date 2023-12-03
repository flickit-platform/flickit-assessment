package org.flickit.assessment.kit.application.port.out.questionnaire;

import java.time.LocalDateTime;

public interface UpdateQuestionnairePort {

    void update(Param param);

    record Param(Long id, String title, Integer index, String description, LocalDateTime lastModificationTime) {
    }
}
