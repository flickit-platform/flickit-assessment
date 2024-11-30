package org.flickit.assessment.advice.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessageKey {

    public static final String ASSESSMENT_ID_NOT_FOUND = "assessment-id.notFound";

    public static final String CREATE_ADVICE_ASSESSMENT_ID_NOT_NULL = "create-advice.assessmentId.notNull";
    public static final String CREATE_ADVICE_ATTRIBUTE_LEVEL_TARGETS_NOT_NULL = "create-advice.attributeLevelTargets.notNull";
    public static final String CREATE_ADVICE_ATTRIBUTE_LEVEL_TARGETS_SIZE_MIN = "create-advice.attributeLevelTargets.size.min";
    public static final String CREATE_ADVICE_ASSESSMENT_RESULT_NOT_FOUND = "create-advice.assessmentResult.notFound";
    public static final String CREATE_ADVICE_ASSESSMENT_RESULT_NOT_VALID = "create-advice.assessmentResult.notValid";
    public static final String CREATE_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION = "create-advice.finding-best-solution.execution";
    public static final String CREATE_ADVICE_ASSESSMENT_ATTRIBUTE_RELATION_NOT_FOUND = "create-advice.assessmentAttributeRelation.notFound";
    public static final String CREATE_ADVICE_ASSESSMENT_LEVEL_RELATION_NOT_FOUND = "create-advice.assessmentLevelRelation.notFound";

    public static final String CREATE_AI_ADVICE_NARRATION_ADVICE_LIST_ITEMS_NOT_NULL = "create-ai-advice-narration.adviceListItems.notNull";
    public static final String CREATE_AI_ADVICE_NARRATION_ATTRIBUTE_LEVEL_TARGETS_NOT_NULL = "create-ai-advice-narration.attributeLevelTargets.notNull";
    public static final String CREATE_AI_ADVICE_NARRATION_ASSESSMENT_ID_NOT_NULL = "create-ai-advice-narration.assessmentId.notNull";
    public static final String CREATE_AI_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND = "create-ai-advice-narration.assessmentResult.notFound";
    public static final String CREATE_AI_ADVICE_NARRATION_ATTRIBUTE_LEVEL_TARGETS_SIZE_MIN = "create-ai-advice-narration.attributeLevelTargets.size.min";


    public static final String GET_ADVICE_NARRATION_ASSESSMENT_ID_NOT_NULL = "get-advice-narration.assessmentId.notNull";
    public static final String GET_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND = "get-advice-narration.assessmentResult.notFound";

    public static final String CREATE_ASSESSOR_ADVICE_NARRATION_ASSESSMENT_ID_NOT_NULL = "create-assessor-advice-narration.assessmentId.notNull";
    public static final String CREATE_ASSESSOR_ADVICE_NARRATION_ASSESSOR_NARRATION_SIZE_MIN = "create-assessor-advice-narration.assessorNarration.size.min";
    public static final String CREATE_ASSESSOR_ADVICE_NARRATION_ASSESSOR_NARRATION_SIZE_MAX = "create-assessor-advice-narration.assessorNarration.size.max";
    public static final String CREATE_ASSESSOR_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND = "create-assessor-advice-narration.assessmentResult.notFound";

    public static final String CREATE_ADVICE_ITEM_ASSESSMENT_ID_NOT_NULL = "create-advice-item.assessmentId.notNull";
    public static final String CREATE_ADVICE_ITEM_TITLE_NOT_NULL = "create-advice-item.title.notNull";
    public static final String CREATE_ADVICE_ITEM_TITLE_SIZE_MIN = "create-advice-item.title.size.min";
    public static final String CREATE_ADVICE_ITEM_TITLE_SIZE_MAX = "create-advice-item.title.size.max";
    public static final String CREATE_ADVICE_ITEM_DESCRIPTION_SIZE_MIN = "create-advice-item.description.size.min";
    public static final String CREATE_ADVICE_ITEM_DESCRIPTION_SIZE_MAX = "create-advice-item.description.size.max";
    public static final String CREATE_ADVICE_ITEM_COST_NOT_NULL = "create-advice-item.cost.notNull";
    public static final String CREATE_ADVICE_ITEM_COST_INVALID = "create-advice-item.cost.invalid";
    public static final String CREATE_ADVICE_ITEM_PRIORITY_NOT_NULL = "create-advice-item.priority.notNull";
    public static final String CREATE_ADVICE_ITEM_PRIORITY_INVALID = "create-advice-item.priority.invalid";
    public static final String CREATE_ADVICE_ITEM_IMPACT_NOT_NULL = "create-advice-item.impact.notNull";
    public static final String CREATE_ADVICE_ITEM_IMPACT_INVALID = "create-advice-item.impact.invalid";
    public static final String CREATE_ADVICE_ITEM_ASSESSMENT_RESULT_NOT_FOUND = "create-advice-item.assessmentResult.notFound";

    public static final String GET_ADVICE_ITEM_LIST_ASSESSMENT_ID_NOT_NULL = "get-advice-item-list.assessmentId.notNull";
    public static final String GET_ADVICE_ITEM_LIST_SIZE_MIN = "get-advice-item-list.size.min";
    public static final String GET_ADVICE_ITEM_LIST_SIZE_MAX = "get-advice-item-list.size.max";
    public static final String GET_ADVICE_ITEM_LIST_PAGE_MIN = "get-advice-item-list.page.min";
    public static final String GET_ADVICE_ITEM_LIST_ASSESSMENT_RESULT_NOT_FOUND = "get-advice-item-list.assessmentResult.notFound";
}
