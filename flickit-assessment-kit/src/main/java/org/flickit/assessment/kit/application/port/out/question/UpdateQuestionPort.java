package org.flickit.assessment.kit.application.port.out.question;

import java.time.LocalDateTime;

public interface UpdateQuestionPort {

    void update(Param param);

    record Param(Long id,
                 String title,
                 Integer index,
                 String description,
                 Boolean mayNotBeApplicable,
                 LocalDateTime lastModificationTime) {
    }
}
