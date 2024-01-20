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
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

@PlanningEntity
@Getter
@Setter
@NoArgsConstructor
public class Question {

    @PlanningId
    private long id;

    private int cost;

    @ProblemFactCollectionProperty
    private List<Option> options;

    @ValueRangeProvider
    private List<Integer> optionIndexValues;

    @PlanningVariable
    private Integer recommendedOptionIndex;

    private int currentOptionIndex;

    public Question(Long id, int cost, List<Option> options, Integer currentOptionIndex) {
        this.id = id;
        this.cost = cost;
        this.options = options;
        this.currentOptionIndex = currentOptionIndex;
        this.optionIndexValues = IntStream.range(currentOptionIndex, options.size()).boxed().toList();
    }

    public double getTargetGain(Target target) {
        return getOptionGain(target, recommendedOptionIndex) - getOptionGain(target, currentOptionIndex);
    }

    public double getCost() {
        return getOptionCost(recommendedOptionIndex) - getOptionCost(currentOptionIndex);
    }

    private double getOptionGain(Target target, Integer optionIndex) {
        return options
            .get(Objects.requireNonNullElseGet(optionIndex, () -> currentOptionIndex))
            .getTargetGain(target);
    }

    private double getOptionCost(Integer optionIndex) {
        return options
            .get(Objects.requireNonNullElseGet(optionIndex, () -> currentOptionIndex))
            .getCost();
    }

    public boolean hasGain() {
        return recommendedOptionIndex != null && recommendedOptionIndex > optionIndexValues.get(0);
    }

    public double getAllTargetsGain() {
        double sum = 0;
        Set<Target> targets = options.get(recommendedOptionIndex).getGains().keySet();
        for (Target target : targets) {
            sum += getOptionGain(target, recommendedOptionIndex);
        }
        return sum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question question)) return false;
        return id == question.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Question{" +
            "id=" + id +
            ", currentOptionIndex=" + currentOptionIndex +
            ", recommendedOptionIndex=" + recommendedOptionIndex +
            '}';
    }
}
