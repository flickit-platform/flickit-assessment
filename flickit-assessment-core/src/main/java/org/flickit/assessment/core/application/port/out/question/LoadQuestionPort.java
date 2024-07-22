package org.flickit.assessment.core.application.port.out.question;

public interface LoadQuestionPort {

    Result loadByIdAndKitVersionId(Long id, Long kitVersionId);

    record Result (Long id,
                   String title,
                   Integer index,
                   Long questionnaireId){}

}
