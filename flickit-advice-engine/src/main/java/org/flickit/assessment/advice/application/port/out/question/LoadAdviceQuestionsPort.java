package org.flickit.assessment.advice.application.port.out.question;

import java.util.List;

public interface LoadAdviceQuestionsPort {
    void load(List<Long> questionIds);
}
