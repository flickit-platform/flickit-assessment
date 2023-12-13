package org.flickit.assessment.core.adapter.out.persistence.constraint;

import org.flickit.assessment.common.exception.handler.DataConstraintErrorMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Component
public class AssessmentCoreDataConstraintErrorMapper implements DataConstraintErrorMapper {

    Map<String, String> constraintToErrorMsg = Map.of(
        "uq_fac_assessment_spaceid_code_deletion_time", CREATE_ASSESSMENT_DUPLICATE_TITLE_AND_SPACE_ID,
        "fk_fac_assessmentresult_assessment", CREATE_ASSESSMENT_RESULT_ASSESSMENT_ID_NOT_FOUND,
        "fk_fac_qualityattributevalue_assessmentresult", CREATE_QUALITY_ATTRIBUTE_VALUE_ASSESSMENT_RESULT_ID_NOT_FOUND,
        "fk_fac_subjectvalue_assessmentresult", CREATE_SUBJECT_VALUE_ASSESSMENT_RESULT_ID_NOT_FOUND,
        "fk_fac_answer_assessmentresult", SUBMIT_ANSWER_ASSESSMENT_RESULT_NOT_FOUND,
        "fk_fac_evidence_assessment", ADD_EVIDENCE_ASSESSMENT_ID_NOT_FOUND);

    @Override
    public boolean contains(String constraintName) {
        return constraintToErrorMsg.containsKey(constraintName);
    }

    @Override
    public String errorMessage(String constraintName) {
        return constraintToErrorMsg.get(constraintName);
    }
}
