package org.flickit.assessment.kit.application.port.out.question;

import java.util.UUID;

public interface CreateQuestionPort {

    Long persist(Param param);

    record Param(String code,
                 String title,
                 String hint,
                 Integer index,
                 Long questionnaireId,
                 Boolean mayNotBeApplicable,
                 UUID currentUserId) {
    }
}
