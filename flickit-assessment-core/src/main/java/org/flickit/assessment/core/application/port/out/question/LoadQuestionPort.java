package org.flickit.assessment.core.application.port.out.question;

import java.util.Optional;

public interface LoadQuestionPort {

    Optional<Result> loadByIdAndKitVersionId(Long id, Long kitVersionId);

    record Result(Long id,
                  String title,
                  Integer index,
                  Long questionnaireId) {
    }
}
