package org.flickit.assessment.advice.application.service;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import ai.timefold.solver.core.api.score.stream.Joiners;
import org.flickit.assessment.advice.application.domain.Question;
import org.flickit.assessment.advice.application.domain.Target;

import java.util.function.Predicate;

import static ai.timefold.solver.core.api.score.stream.ConstraintCollectors.count;
import static ai.timefold.solver.core.api.score.stream.ConstraintCollectors.sum;

public class PlanConstraintProvider implements ConstraintProvider {

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
            .forEach(Target.class)
            .join(Question.class,
                Joiners.filtering((t, q) -> q.hasImpact(t))
            )
            .groupBy(
                (t, q) -> t,
                sum((t, q) -> (int) (Math.floor(q.getTargetGain(t)))))
            .filter((t, sum) -> t.getNeededGain() - sum > 0)
            .penalize(HardSoftScore.ONE_HARD,
                (t, sum) -> (int) Math.ceil(t.getNeededGain()) - sum)
            .asConstraint("minGain");
    }

    Constraint totalBenefit(ConstraintFactory constraintFactory) {
        return constraintFactory
            .forEach(Question.class)
            .filter(isQuestionOnPlan())
            .groupBy(
                sum(q -> (int) (Math.floor(q.getAllGains()))),
                sum(q -> (int) (Math.ceil(q.getCost())))
            )
            .reward(HardSoftScore.ONE_SOFT,
                (totalGain, totalCost) -> Math.round(((float) totalGain / totalCost) * 100))
            .asConstraint("totalBenefit");
    }

    Constraint leastCount(ConstraintFactory constraintFactory) {
        return constraintFactory
            .forEach(Question.class)
            .filter(isQuestionOnPlan())
            .groupBy(count())
            .filter(count -> count > 0)
            .penalize(HardSoftScore.ONE_SOFT, c -> c)
            .asConstraint("leastCount");
    }

    private Predicate<Question> isQuestionOnPlan() {
        return Question::isRecommended;
    }
}
