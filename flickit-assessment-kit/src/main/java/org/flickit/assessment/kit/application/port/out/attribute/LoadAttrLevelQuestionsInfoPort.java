package org.flickit.assessment.kit.application.port.out.attribute;

import java.util.List;

public interface LoadAttrLevelQuestionsInfoPort {

    Result loadAttrLevelQuestionsInfo(Long attributeId, Long maturityLevelId);

    record Result(Long id, String title, int index, int questionsCount, List<Question> questions) {

        public record Question(
            int index,
            String title,
            boolean mayNotBeApplicable,
            int weight,
            String questionnaire,
            List<AnswerOption> answerOption
        ) {

            public record AnswerOption(int index, String title, double value) {}
        }
    }
}
