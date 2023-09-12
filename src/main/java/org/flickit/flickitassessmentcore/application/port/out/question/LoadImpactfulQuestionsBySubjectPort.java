package org.flickit.flickitassessmentcore.application.port.out.question;

import org.flickit.flickitassessmentcore.adapter.out.rest.question.QuestionDto;

import java.util.List;

public interface LoadImpactfulQuestionsBySubjectPort {

    List<QuestionDto> loadImpactfulQuestionsBySubjectId(long subjectId);
}
