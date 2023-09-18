package org.flickit.flickitassessmentcore.application.port.out.question;

import org.flickit.flickitassessmentcore.application.domain.Question;

import java.util.List;

public interface LoadQuestionsBySubjectPort {

    List<Question> loadImpactfulQuestionsBySubjectId(long subjectId);
}
