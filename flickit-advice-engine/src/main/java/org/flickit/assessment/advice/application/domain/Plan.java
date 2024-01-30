package org.flickit.assessment.advice.application.domain;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@PlanningSolution
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Plan {

    @ProblemFactCollectionProperty
    private List<AttributeLevelScore> attributeLevelScores;

    @PlanningEntityCollectionProperty
    private List<Question> questions;

    @PlanningScore
    private HardSoftScore score;

    public Plan(List<AttributeLevelScore> attributeLevelScores, List<Question> questions) {
        this.attributeLevelScores = attributeLevelScores;
        this.questions = questions;
    }
}
