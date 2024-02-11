package org.flickit.assessment.advice.application.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@PlanningEntity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Question {

    @PlanningId
    @EqualsAndHashCode.Include
    private long id;

    private int cost;

    @ProblemFactCollectionProperty
    private List<Option> options;

    @PlanningVariable
    private Integer recommendedOptionIndex;

    private Integer currentOptionIndex;

    public Question(Long id, int cost, List<Option> options, Integer currentOptionIndex) {
        this.id = id;
        this.cost = cost;
        this.options = options;
        this.currentOptionIndex = currentOptionIndex;
    }

    @ValueRangeProvider
    private List<Integer> getNextOptionIndexes() {
        return IntStream.range(getCurrentOptionIndexValue(), options.size()).boxed().toList();
    }

    public int getCurrentOptionIndexValue() {
        return currentOptionIndex != null ? currentOptionIndex : 0;
    }

    public double calculateGainingScore(AttributeLevelScore attributeLevelScore) {
        return getOptionScore(attributeLevelScore, recommendedOptionIndex)
            - getOptionScore(attributeLevelScore, getCurrentOptionIndexValue());
    }

    public double getCost() {
        return getOptionCost(recommendedOptionIndex) - getOptionCost(getCurrentOptionIndexValue());
    }

    public boolean isRecommended() {
        return recommendedOptionIndex != null && recommendedOptionIndex > getCurrentOptionIndexValue();
    }

    public boolean hasImpact(AttributeLevelScore attributeLevelScore) {
        return options.stream().anyMatch(op -> op.hasImpact(attributeLevelScore));
    }

    public double calculateGainingScore() {
        return options.get(recommendedOptionIndex).sumScores() - options.get(getCurrentOptionIndexValue()).sumScores();
    }

    public double calculateBenefit() {
        return calculateGainingScore() / getOptionCost(getRecommendedOptionIndex());
    }

    private double getOptionScore(AttributeLevelScore attributeLevelScore, Integer optionIndex) {
        return options
            .get(Objects.requireNonNullElseGet(optionIndex, this::getCurrentOptionIndexValue))
            .getAttributeLevelPromisedScore(attributeLevelScore);
    }

    private double getOptionCost(Integer optionIndex) {
        return options
            .get(Objects.requireNonNullElseGet(optionIndex, this::getCurrentOptionIndexValue))
            .getCost();
    }
}
