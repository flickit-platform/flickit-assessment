package org.flickit.assessment.core.application.port.out.answer;

import java.util.Set;

public interface DeleteAnswerPort {

    void delete(Set<Long> questionIds);
}
