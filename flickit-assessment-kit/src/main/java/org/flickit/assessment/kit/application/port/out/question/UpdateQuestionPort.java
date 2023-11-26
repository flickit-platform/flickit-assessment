package org.flickit.assessment.kit.application.port.out.question;

public interface UpdateQuestionPort {

    void update(Param param);

    record Param(Long id, String title, String description, Integer index, Boolean isNotApplicable) {
    }
}
