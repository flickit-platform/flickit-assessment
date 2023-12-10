package org.flickit.assessment.kit.application.port.out.question;

public interface CreateQuestionPort {

    Long persist(Param param);

    record Param(String code,
                 String title,
                 String description,
                 Integer index,
                 Long questionnaireId,
                 Boolean mayNotBeApplicable) {
    }
}
