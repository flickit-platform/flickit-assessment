package org.flickit.assessment.advice.application.service.advice;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import ai.timefold.solver.core.api.score.stream.Joiners;
import org.flickit.assessment.advice.application.domain.AttributeLevelScore;
import org.flickit.assessment.advice.application.domain.Question;

import java.util.function.Predicate;

import static ai.timefold.solver.core.api.score.stream.ConstraintCollectors.count;
import static ai.timefold.solver.core.api.score.stream.ConstraintCollectors.sum;

public class PlanConstraintProvider implements ConstraintProvider {

    public static final int SOFT_SCORE_FACTOR = 100;

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
            // Hard constraints
            minGain(constraintFactory),
            // Soft constraints
            totalBenefit(constraintFactory),
            leastCount(constraintFactory)
        };
    }

    Constraint minGain(ConstraintFactory constraintFactory) {
        return constraintFactory
            .forEach(AttributeLevelScore.class)
            .join(Question.class,
                Joiners.filtering((t, q) -> q.hasImpact(t))
            )
            .groupBy(
                (t, q) -> t,
                sum((t, q) -> (int) (Math.floor(q.calculateGainingScore(t)))))
            .filter((t, sum) -> t.getRemainingScore() - sum > 0)
            .penalize(HardSoftScore.ONE_HARD,
                (t, sum) -> (int) Math.ceil(t.getRemainingScore()) - sum)
            .asConstraint("minGain");
    }

    Constraint totalBenefit(ConstraintFactory constraintFactory) {
        return constraintFactory
            .forEach(Question.class)
            .filter(isQuestionOnPlan())
            .groupBy(
                sum(q -> (int) (Math.floor(q.calculateGainingScore()))),
                sum(q -> (int) (Math.ceil(q.getCost())))
            )
            .reward(HardSoftScore.ONE_SOFT,
                (totalGain, totalCost) -> Math.round(((float) totalGain / totalCost) * 100 * SOFT_SCORE_FACTOR))
            .asConstraint("totalBenefit");
    }

    Constraint leastCount(ConstraintFactory constraintFactory) {
        return constraintFactory
            .forEach(Question.class)
            .filter(isQuestionOnPlan())
            .groupBy(count())
            .filter(count -> count > 0)
            .penalize(HardSoftScore.ONE_SOFT, c -> c * SOFT_SCORE_FACTOR)
            .asConstraint("leastCount");
    }

    private Predicate<Question> isQuestionOnPlan() {
        return Question::isRecommended;
    }
}
