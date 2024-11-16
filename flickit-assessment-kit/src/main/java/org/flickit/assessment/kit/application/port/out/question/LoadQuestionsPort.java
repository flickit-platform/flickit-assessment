package org.flickit.assessment.kit.application.port.out.question;

import org.flickit.assessment.kit.application.domain.Question;

import java.util.List;

public interface LoadQuestionsPort {

    List<Question> loadAllByKitVersionId(long kitVersionId);

    List<Question> loadQuestionsWithoutImpact (long kitVersionId);
}
