package org.flickit.flickitassessmentcore.application.port.out.answer;

public interface UpdateAnswerPort {

    void update(Param param);

    record Param(Long answerOptionId, boolean isNotApplicable) {
    }
}
