package org.flickit.assessment.advice.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessageKey {

    public static final String CREATE_ADVICE_ASSESSMENT_ID_NOT_NULL = "create-advice.assessmentId.notNull";
    public static final String CREATE_ADVICE_ATTRIBUTE_LEVEL_TARGETS_NOT_NULL = "create-advice.attributeLevelTargets.notNull";
    public static final String CREATE_ADVICE_ATTRIBUTE_LEVEL_TARGETS_SIZE_MIN = "create-advice.attributeLevelTargets.size.min";
    public static final String CREATE_ADVICE_ASSESSMENT_RESULT_NOT_FOUND = "create-advice.assessmentResult.notFound";
    public static final String CREATE_ADVICE_ASSESSMENT_RESULT_NOT_VALID = "create-advice.assessmentResult.notValid";
    public static final String CREATE_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION = "create-advice.finding-best-solution.execution";
    public static final String CREATE_ADVICE_ASSESSMENT_ATTRIBUTE_RELATION_NOT_FOUND =  "create-advice.assessmentAttributeRelation.notFound";
    public static final String CREATE_ADVICE_ASSESSMENT_LEVEL_RELATION_NOT_FOUND =  "create-advice.assessmentLevelRelation.notFound";

    public static final String CREATE_ADVICE_NARRATION_ADVICE_LIST_ITEMS_NOT_NULL = "create-advice-narration.adviceListItems.notNull";
    public static final String CREATE_ADVICE_NARRATION_ATTRIBUTE_LEVEL_TARGETS_NOT_NULL = "create-advice-narration.attributeLevelTargets.notNull";
    public static final String CREATE_ADVICE_NARRATION_ASSESSMENT_ID_NOT_NULL = "create-advice-narration.assessmentId.notNull";
}
