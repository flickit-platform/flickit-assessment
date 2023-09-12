package org.flickit.flickitassessmentcore.application.port.out.answer;

import java.util.List;
import java.util.UUID;

public interface CountAnswersByQuestionAndAssessmentResultPort {

    int countAnswersByQuestionIdAndAssessmentResult(List<Long> questionIds, UUID resultId);

}
