package org.flickit.assessment.users.adapter.out.persistence.constraint;

import org.flickit.assessment.common.exception.handler.DataConstraintErrorMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.util.Map.entry;
import static org.flickit.assessment.users.common.ErrorMessageKey.CREATE_EXPERT_GROUP_TITLE_DUPLICATE;
import static org.flickit.assessment.users.common.ErrorMessageKey.CREATE_SPACE_TITLE_DUPLICATE;

@Component
public class AssessmentUsersDataConstraintErrorMapper implements DataConstraintErrorMapper {

    Map<String, String> constraintToErrorMsg = Map.ofEntries(
        entry("uq_fau_expert_group_code_deletion_time", CREATE_EXPERT_GROUP_TITLE_DUPLICATE),
        entry("uq_fau_space_code_createdby", CREATE_SPACE_TITLE_DUPLICATE)
    );

    @Override
    public boolean contains(String constraintName) {
        return constraintToErrorMsg.containsKey(constraintName);
    }

    @Override
    public String errorMessage(String constraintName) {
        return constraintToErrorMsg.get(constraintName);
    }
}
