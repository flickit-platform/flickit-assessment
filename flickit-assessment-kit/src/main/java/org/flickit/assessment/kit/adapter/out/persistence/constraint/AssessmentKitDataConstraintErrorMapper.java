package org.flickit.assessment.kit.adapter.out.persistence.constraint;

import org.flickit.assessment.common.exception.handler.DataConstraintErrorMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.util.Map.entry;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@Component
public class AssessmentKitDataConstraintErrorMapper implements DataConstraintErrorMapper {

    Map<String, String> constraintToErrorMsg = Map.ofEntries(
        entry("uq_fak_assessmentkit_title", CREATE_KIT_BY_DSL_KIT_TITLE_DUPLICATE),
        entry("uq_fak_assessmentkit_code", CREATE_KIT_BY_DSL_KIT_TITLE_DUPLICATE),
        entry("fk_fak_kit_user_access_account_user", GRANT_USER_ACCESS_TO_KIT_USER_ID_NOT_FOUND),
        entry("fk_fak_kit_user_access_assessmentkit", GRANT_USER_ACCESS_TO_KIT_KIT_ID_NOT_FOUND),
        entry("fak_kit_user_access_pkey", GRANT_USER_ACCESS_TO_KIT_USER_ID_DUPLICATE),
        entry("fk_fak_kittagrelation_tag", UPDATE_KIT_INFO_TAG_ID_NOT_FOUND),
        entry("uq_fak_subject_index_kitversionid", CREATE_SUBJECT_INDEX_DUPLICATE),
        entry("uq_fak_subject_title_kitversionid", CREATE_SUBJECT_TITLE_DUPLICATE),
        entry("uq_fak_subject_code_kitversionid", CREATE_SUBJECT_CODE_DUPLICATE),
        entry("uq_fak_attribute_kitversionid_code_subjectid", CREATE_ATTRIBUTE_CODE_DUPLICATE),
        entry("uq_fak_attribute_kitversionid_index_subjectid", CREATE_ATTRIBUTE_INDEX_DUPLICATE),
        entry("uq_fak_attribute_code_kitversionid", CREATE_ATTRIBUTE_CODE_DUPLICATE),
        entry("uq_fak_maturitylevel_code_kitversionid", CREATE_MATURITY_LEVEL_CODE_DUPLICATE),
        entry("uq_fak_maturitylevel_index_kitversionid", CREATE_MATURITY_LEVEL_INDEX_DUPLICATE),
        entry("uq_fak_maturitylevel_title_kitversionid", CREATE_MATURITY_LEVEL_TITLE_DUPLICATE),
        entry("uq_fak_maturitylevel_value_kitversionid", CREATE_MATURITY_LEVEL_VALUE_DUPLICATE),
        entry("uq_fak_questionnaire_code_kitversionid", CREATE_QUESTIONNAIRE_TITLE_DUPLICATE),
        entry("uq_fak_questionnaire_index_kitversionid", CREATE_QUESTIONNAIRE_INDEX_DUPLICATE),
        entry("uq_fak_questionnaire_title_kitversionid", CREATE_QUESTIONNAIRE_TITLE_DUPLICATE),
        entry("fk_fak_levelcompetence_maturitylevel_effectivelevelid", MATURITY_LEVEL_ID_NOT_FOUND),
        entry("fk_fak_levelcompetence_maturitylevel_affectedlevelid", MATURITY_LEVEL_ID_NOT_FOUND),
        entry("uq_fak_levelcompetence_affectedlevel_effectivelevel_kitversion", CREATE_LEVEL_COMPETENCE_DUPLICATE),
        entry("fk_fak_levelcompetence_kitversion", KIT_VERSION_ID_NOT_FOUND),
        entry("fk_fak_attribute_subject", SUBJECT_ID_NOT_FOUND),
        entry("uq_fak_question_code_kitversionid_questionnaireid", CREATE_QUESTION_INDEX_DUPLICATE),
        entry("uq_fak_question_index_kitversionid_questionnaireid", CREATE_QUESTION_INDEX_DUPLICATE),
        entry("fk_fak_question_questionnaire", QUESTIONNAIRE_ID_NOT_FOUND),
        entry("fk_fak_questionimpact_attribute", ATTRIBUTE_ID_NOT_FOUND),
        entry("fk_fak_questionimpact_question", QUESTION_ID_NOT_FOUND),
        entry("fk_fak_questionimpact_maturitylevel", MATURITY_LEVEL_ID_NOT_FOUND),
        entry("uq_fak_question_impact_question_attribute_level_kitversion", CREATE_QUESTION_IMPACT_DUPLICATE),
        entry("uq_fak_answeroption_index_kitversionid_answerrangeid", CREATE_ANSWER_OPTION_INDEX_DUPLICATE),
        entry("fk_fak_answeroption_question", QUESTION_ID_NOT_FOUND),
        entry("uq_fak_kitcustom_title_kitid", CREATE_KIT_CUSTOM_TITLE_DUPLICATE),
        entry("uq_fak_kitcustom_code_kitid", CREATE_KIT_CUSTOM_TITLE_DUPLICATE),
        entry("uq_fak_kitversion_kitId_statusversion", CREATE_ASSESSMENT_KIT_STATUS_DUPLICATE));

    @Override
    public boolean contains(String constraintName) {
        return constraintToErrorMsg.containsKey(constraintName);
    }

    @Override
    public String errorMessage(String constraintName) {
        return constraintToErrorMsg.get(constraintName);
    }
}
