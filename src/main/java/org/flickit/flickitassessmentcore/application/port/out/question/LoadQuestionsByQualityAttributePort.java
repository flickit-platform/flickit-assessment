package org.flickit.flickitassessmentcore.application.port.out.question;

import org.flickit.flickitassessmentcore.domain.Question;

import java.util.List;

public interface LoadQuestionsByQualityAttributePort {

    Result loadByQualityAttributeId(Param param);

    record Param(Long qualityAttributeId) {}

    record Result(List<Question> questions) {}
}
