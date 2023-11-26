package org.flickit.assessment.kit.application.port.out.question;

import org.flickit.assessment.kit.application.domain.Question;

import java.util.List;

public interface LoadQuestionsByKitPort {

    List<Question> loadByKit(Long kitId);
}
