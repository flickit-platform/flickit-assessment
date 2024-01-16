package application.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@PlanningEntity
@Getter
@Setter
@NoArgsConstructor
public class Question {

    @PlanningId
    private Long id;
    private int gain;
    private int cost;

    @ProblemFactCollectionProperty
    @ValueRangeProvider
    private List<Double> options;

    @PlanningVariable
    private Double option;

    private Double currentOption;

    private Target target;

    public Question(Long id, Target target, int gain, int cost, Double currentOption, List<Double> options) {
        this.id = id;
        this.gain = gain;
        this.cost = cost;
        this.options = options;
        this.target = target;
        this.currentOption = currentOption;
    }

    public double getGainRatio() {
        return option - currentOption;
    }

    @Override
    public String toString() {
        return "Question{" +
                "gain=" + gain +
                ", cost=" + cost +
                ", option=" + option +
                ", currentOption=" + currentOption +
                '}';
    }
}
