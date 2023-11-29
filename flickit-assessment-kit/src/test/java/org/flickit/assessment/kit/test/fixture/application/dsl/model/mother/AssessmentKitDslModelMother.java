package org.flickit.assessment.kit.test.fixture.application.dsl.model.mother;

import org.flickit.assessment.kit.application.domain.dsl.*;

import java.util.List;

public class AssessmentKitDslModelMother {

    public static AssessmentKitDslModel assessmentKitDslModel(
        List<QuestionnaireDslModel> questionnaires,
        List<AttributeDslModel> attributes,
        List<QuestionDslModel> questions,
        List<SubjectDslModel> subjects,
        List<MaturityLevelDslModel> maturityLevels
    ) {
        AssessmentKitDslModel dslKit = new AssessmentKitDslModel();
        dslKit.setQuestionnaires(questionnaires);
        dslKit.setAttributes(attributes);
        dslKit.setQuestions(questions);
        dslKit.setSubjects(subjects);
        dslKit.setMaturityLevels(maturityLevels);
        return dslKit;
    }
}
