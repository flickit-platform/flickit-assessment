package org.flickit.flickitassessmentcore.application.port.out;

import org.flickit.flickitassessmentcore.application.port.in.answer.GetAnswerListUseCase.AnswerItem;

import java.util.List;
import java.util.UUID;

public interface LoadAnswersByAssessmentAndQuestionnaireIdPort {

    List<AnswerItem> loadAnswersByAssessmentAndQuestionnaireIdPort(Param param);

    record Param(UUID assessmentId, Long questionnaireId) {
    }
}
