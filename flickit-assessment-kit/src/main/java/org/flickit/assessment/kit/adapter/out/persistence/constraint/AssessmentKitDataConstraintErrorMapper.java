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
        entry("uq_fak_questionnaire_code_kitversionid", CREATE_QUESTIONNAIRE_CODE_DUPLICATE),
        entry("uq_fak_questionnaire_index_kitversionid", CREATE_QUESTIONNAIRE_INDEX_DUPLICATE),
        entry("uq_fak_questionnaire_title_kitversionid", CREATE_QUESTIONNAIRE_TITLE_DUPLICATE));

    @Override
    public boolean contains(String constraintName) {
        return constraintToErrorMsg.containsKey(constraintName);
    }

    @Override
    public String errorMessage(String constraintName) {
        return constraintToErrorMsg.get(constraintName);
    }
}
