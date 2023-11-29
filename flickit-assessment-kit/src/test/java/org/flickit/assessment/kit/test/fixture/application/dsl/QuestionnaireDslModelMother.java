package org.flickit.assessment.kit.test.fixture.application.dsl;

import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;

import java.util.function.Consumer;

public class QuestionnaireDslModelMother {

    public static QuestionnaireDslModel domainToDslModel(Questionnaire questionnaire) {
        return domainToDslModel(questionnaire, b -> {
        });
    }

    public static QuestionnaireDslModel domainToDslModel(Questionnaire questionnaire,
                                                         Consumer<QuestionnaireDslModel.QuestionnaireDslModelBuilder<?, ?>> changer) {
        var builder = domainToDslModelBuilder(questionnaire);
        changer.accept(builder);
        return builder.build();
    }

    private static QuestionnaireDslModel.QuestionnaireDslModelBuilder<?, ?> domainToDslModelBuilder(Questionnaire questionnaire) {
        return QuestionnaireDslModel.builder()
            .code(questionnaire.getCode())
            .title(questionnaire.getTitle())
            .index(questionnaire.getIndex())
            .description(questionnaire.getDescription());
    }
}
