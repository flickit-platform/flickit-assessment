package application.service;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import application.domain.Question;
import application.domain.Target;

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
            .filter(target -> target.getScore() > 0)
            .penalize(HardSoftScore.ONE_HARD,
                Target::getScore)
            .asConstraint("minGain");
    }

    Constraint totalBenefit(ConstraintFactory constraintFactory) {
        return constraintFactory
            .forEach(Question.class)
            .filter(isQuestionOnPlan())
            .groupBy(
                sum(q -> (int) (Math.round(q.getAllTargetsGain()))),
                sum(q -> (int) (Math.round(q.getCost())))
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
        return Question::hasGain;
    }
}
