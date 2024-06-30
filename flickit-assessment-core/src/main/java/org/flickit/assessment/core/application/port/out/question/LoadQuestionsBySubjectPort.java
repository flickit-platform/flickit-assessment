package org.flickit.assessment.core.application.port.out.question;

import org.flickit.assessment.core.application.domain.Question;

import java.util.List;

public interface LoadQuestionsBySubjectPort {

    List<Question> loadQuestionsBySubject(long subjectId, long kitVersionId);
}
