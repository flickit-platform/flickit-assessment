package org.flickit.assessment.kit.application.port.out.questionnaire;

import java.util.List;

public interface LoadKitQuestionnaireDetailPort {

    Result loadKitQuestionnaireDetail(Long questionnaireId, Long kitId);

    record Result(int questionsCount, List<String> relatedSubjects, String description, List<Question> questions) {
        public record Question(Long id, String title, Integer index, Boolean mayNotBeApplicable) {}
    }
}
