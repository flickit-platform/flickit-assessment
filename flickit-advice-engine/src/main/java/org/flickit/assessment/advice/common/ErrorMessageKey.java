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

    public static final String REFRESH_ASSESSMENT_ADVICE_ASSESSMENT_ID_NOT_NULL = "refresh-assessment-advice.assessmentId.notNull";
    public static final String REFRESH_ASSESSMENT_ADVICE_FORCE_REGENERATE_NOT_NULL = "refresh-assessment-advice.forceRegenerate.notNull";
    public static final String REFRESH_ASSESSMENT_ADVICE_MEDIAN_MATURITY_LEVEL_NOT_FOUND = "refresh-assessment-advice.median-maturityLevel.notFound";

    public static final String APPROVE_ADVICE_NARRATION_ASSESSMENT_ID_NOT_NULL = "approve-advice-narration.assessmentId.notNull";
    public static final String APPROVE_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND = "approve-advice-narration.assessmentResult.notFound";


}
