package org.flickit.flickitassessmentcore.application.port.out.answer;

import java.util.UUID;

public interface FindAnswerOptionIdByResultAndQuestionInAnswerPort {

    Long findAnswerOptionIdByResultIdAndQuestionId(Param param);

    record Param(UUID resultId, Long questionId) {}
}
