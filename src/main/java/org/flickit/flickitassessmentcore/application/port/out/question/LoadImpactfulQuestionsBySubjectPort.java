package org.flickit.flickitassessmentcore.application.port.out.question;

import org.flickit.flickitassessmentcore.adapter.out.rest.question.ImpactfulQuestionDto;

import java.util.List;

public interface LoadImpactfulQuestionsBySubjectPort {

    List<ImpactfulQuestionDto> loadImpactfulQuestionsBySubjectId(long subjectId);
}
