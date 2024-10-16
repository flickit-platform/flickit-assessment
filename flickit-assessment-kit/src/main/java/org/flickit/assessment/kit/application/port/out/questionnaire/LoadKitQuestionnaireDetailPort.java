package org.flickit.assessment.kit.application.port.out.questionnaire;

import org.flickit.assessment.kit.application.domain.Question;

import java.util.List;

public interface LoadKitQuestionnaireDetailPort {

    Result loadKitQuestionnaireDetail(Long questionnaireId, Long kitVersionId);

    record Result(int questionsCount, List<String> relatedSubjects, String description, List<Question> questions) {}
}
