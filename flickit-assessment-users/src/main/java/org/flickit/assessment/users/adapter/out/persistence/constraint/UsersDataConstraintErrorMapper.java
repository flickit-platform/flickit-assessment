package org.flickit.assessment.users.adapter.out.persistence.constraint;

import org.flickit.assessment.common.exception.handler.DataConstraintErrorMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.util.Map.entry;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;

@Component
public class UsersDataConstraintErrorMapper implements DataConstraintErrorMapper {

    Map<String, String> constraintToErrorMsg = Map.ofEntries(
        entry("baseinfo_expertgroup_name_key", CREATE_EXPERT_GROUP_TITLE_DUPLICATE),
        entry("baseinfo_expertgroupaccess_user_id_3c79acf7_fk_account_user_id", INVITE_EXPERT_GROUP_MEMBER_USER_ID_NOT_FOUND),
        entry("uq_baseinfo_expertgroupaccess_expertgroupid_userid", INVITE_EXPERT_GROUP_MEMBER_EXPERT_GROUP_ID_USER_ID_UNIQUE));

    @Override
    public boolean contains(String constraintName) {
        return constraintToErrorMsg.containsKey(constraintName);
    }

    @Override
    public String errorMessage(String constraintName) {
        return constraintToErrorMsg.get(constraintName);
    }
}
