package org.flickit.flickitassessmentcore.domain.calculate.mother;

import org.flickit.flickitassessmentcore.domain.calculate.QualityAttribute;
import org.flickit.flickitassessmentcore.domain.calculate.QualityAttributeValue;
import org.flickit.flickitassessmentcore.domain.calculate.Question;
import org.flickit.flickitassessmentcore.domain.calculate.QuestionImpact;

import java.util.List;

public class QualityAttributeMother {

    private static long id = 134L;

    public static QualityAttribute.QualityAttributeBuilder builder(){
        return QualityAttribute.builder()
            .id(id++);
    }

    public static QualityAttribute withWeight(int weight) {
        return QualityAttribute.builder()
            .id(id++)
            .weight(weight)
            .build();
    }

    public static QualityAttribute.QualityAttributeBuilder builderWithQuestionsOnLevel23() {
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel23(),
            QuestionMother.withImpactsOnLevel23(),
            QuestionMother.withImpactsOnLevel23(),
            QuestionMother.withImpactsOnLevel23(),
            QuestionMother.withImpactsOnLevel23());

        return QualityAttribute.builder()
            .id(id++)
            .questions(questions);
    }

    public static QualityAttribute.QualityAttributeBuilder builderWithQuestionsOnLevel24() {
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel24());

        return QualityAttribute.builder()
            .id(id++)
            .questions(questions);
    }

}
