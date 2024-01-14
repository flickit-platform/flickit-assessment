package org.flickit.assessment.kit.adapter.out.persistence.expertgroup;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupWithDetailsView;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupListUseCase.Member;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupListPort.Result;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpertGroupMapper {

    public static Result mapToPortResult(ExpertGroupWithDetailsView entity, List<Member> members) {
        return new Result(
            entity.getId(),
            entity.getName(),
            entity.getBio(),
            entity.getPicture(),
            entity.getPublishedKitsCount(),
            entity.getMembersCount(),
            members,
            entity.getOwnerId());
    }
}
