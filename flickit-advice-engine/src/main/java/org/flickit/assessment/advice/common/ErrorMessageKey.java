package org.flickit.assessment.advice.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessageKey {

    public static final String CALCULATE_ADVICE_ASSESSMENT_ID_NOT_NULL = "calculate-advice.assessmentId.notNull";
    public static final String CALCULATE_ADVICE_ATTRIBUTE_LEVEL_TARGETS_NOT_NULL = "calculate-advice.attributeLevelTargets.notNull";
    public static final String CALCULATE_ADVICE_ATTRIBUTE_LEVEL_TARGETS_SIZE_MIN = "calculate-advice.attributeLevelTargets.size.min";
    public static final String CALCULATE_ADVICE_ASSESSMENT_RESULT_NOT_FOUND = "calculate-advice.assessmentResult.notFound";
    public static final String CALCULATE_ADVICE_ASSESSMENT_NOT_FOUND = "calculate-advice.assessment.notFound";
    public static final String CALCULATE_ADVICE_ASSESSMENT_RESULT_NOT_VALID = "calculate-advice.assessmentResult.notValid";
    public static final String CALCULATE_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION = "calculate-advice.finding-best-solution.execution";
    public static final String CALCULATE_ADVICE_ASSESSMENT_ATTRIBUTE_RELATION_NOT_FOUND = "calculate-advice.assessmentAttributeRelation.notFound";
    public static final String CALCULATE_ADVICE_ASSESSMENT_LEVEL_RELATION_NOT_FOUND = "calculate-advice.assessmentLevelRelation.notFound";

    public static final String CREATE_ADVICE_ASSESSMENT_ID_NOT_NULL = "create-advice.assessmentId.notNull";
    public static final String CREATE_ADVICE_ATTRIBUTE_LEVEL_TARGETS_NOT_NULL = "create-advice.attributeLevelTargets.notNull";
    public static final String CREATE_ADVICE_ATTRIBUTE_LEVEL_TARGETS_SIZE_MIN = "create-advice.attributeLevelTargets.size.min";
    public static final String CREATE_ADVICE_ADVICE_QUESTIONS_NOT_NULL = "create-advice.adviceQuestions.notNull";
    public static final String CREATE_ADVICE_ADVICE_QUESTIONS_SIZE_MIN = "create-advice.adviceQuestions.size.min";
}
