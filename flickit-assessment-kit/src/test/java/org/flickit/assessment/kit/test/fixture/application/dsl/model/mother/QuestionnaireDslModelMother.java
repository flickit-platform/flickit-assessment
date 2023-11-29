package org.flickit.assessment.kit.test.fixture.application.dsl.model.mother;

import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;

public class QuestionnaireDslModelMother {

    public static QuestionnaireDslModel questionnaireDslModel(String code, Integer index, String title, String description) {
        QuestionnaireDslModel dslQuestionnaire = new QuestionnaireDslModel();
        dslQuestionnaire.setCode(code);
        dslQuestionnaire.setIndex(index);
        dslQuestionnaire.setTitle(title);
        dslQuestionnaire.setDescription(description);
        return dslQuestionnaire;
    }
}
