package org.flickit.assessment.kit.adapter.out.persistence.constraint;

import org.flickit.assessment.common.exception.handler.DataConstraintErrorMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.util.Map.entry;
import static org.flickit.assessment.kit.common.ErrorMessageKey.CREATE_EXPERT_GROUP_DUPLICATE_TITLE;

@Component
public class AssessmentKitDataConstraintErrorMapper implements DataConstraintErrorMapper {

    Map<String, String> constraintToErrorMsg = Map.ofEntries(
        entry("baseinfo_expertgroup_name_key", CREATE_EXPERT_GROUP_DUPLICATE_TITLE));


    @Override
    public boolean contains(String constraintName) {
        return constraintToErrorMsg.containsKey(constraintName);
    }

    @Override
    public String errorMessage(String constraintName) {
        return constraintToErrorMsg.get(constraintName);
    }
}
