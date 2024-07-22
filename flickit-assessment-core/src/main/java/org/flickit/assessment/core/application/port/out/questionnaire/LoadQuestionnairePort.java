package org.flickit.assessment.core.application.port.out.questionnaire;

import java.util.Optional;

public interface LoadQuestionnairePort {

    Optional<Result> loadByIdAndKitVersionId(Long id, Long kitVersionId);

    record Result(Long id, String title){}
}
