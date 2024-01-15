package application.domain;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactProperty;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import lombok.*;

import java.util.List;


@PlanningSolution
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Plan {

    @ProblemFactProperty
    @ValueRangeProvider
    private Target target;

    @PlanningEntityCollectionProperty
    private List<Question> questions;

    @PlanningScore
    private HardSoftScore score;

    public Plan(Target target, List<Question> questions) {
        this.target = target;
        this.questions = questions;
    }
}
