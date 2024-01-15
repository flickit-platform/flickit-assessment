package application.service;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import ai.timefold.solver.core.api.score.stream.Joiners;
import application.domain.Question;
import application.domain.Target;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntBiFunction;

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
            .forEach(Question.class)
            .join(Target.class,
                Joiners.equal(Question::getTarget, Function.identity())
            )
            .groupBy((question, target) -> target, sum(getQuestionScore()))
            .filter((target, totalGain) -> totalGain < target.getMinGain())
            .penalize(HardSoftScore.ONE_HARD,
                (target, sum) -> target.getMinGain() - sum)
            .asConstraint("minGain");
    }

    Constraint totalBenefit(ConstraintFactory constraintFactory) {
        return constraintFactory
            .forEach(Question.class)
            .filter(isQuestionOnPlan())
            .groupBy(
                Question::getTarget,
                sum(q -> (int) (Math.round(q.getGain() * q.getGainRatio()))),
                sum(q -> (int) (Math.round(q.getCost() * q.getGainRatio()))))
            .reward(HardSoftScore.ONE_SOFT,
                (target, totalGain, totalCost) -> Math.round(((float) totalGain / totalCost) * 100))
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
        return q -> q.getGainRatio() > 0;
    }

    private ToIntBiFunction<Question, Target> getQuestionScore() {
        return (q, tgt) -> (int) Math.floor(q.getGain() * q.getGainRatio());
    }
}
