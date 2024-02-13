package org.flickit.assessment.kit.adapter.out.persistence.expertgroup;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.expertgroup.MembersView;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.LoadExpertGroupMembersPort.Result;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MembersMapper {

    public static Result mapToResult(MembersView entity) {
        return new Result(
            entity.getId(),
            entity.getEmail(),
            entity.getDisplayName(),
            entity.getBio(),
            entity.getPicture(),
            entity.getLinkedin());
    }
}
