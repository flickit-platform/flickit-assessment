package org.flickit.assessment.kit.adapter.out.persistence.expertgroup;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupWithDetailsView;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupListUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupListPort.Result;
import org.flickit.assessment.kit.application.port.out.expertgroup.UpdateExpertGroupPort;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpertGroupMapper {

    public static Result mapToPortResult(ExpertGroupWithDetailsView entity, List<GetExpertGroupListUseCase.Member> members) {
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

    public static ExpertGroupJpaEntity mapUpdateParamToJpaEntity(UpdateExpertGroupPort.Param param) {
        return new ExpertGroupJpaEntity(
            param.id(),
            param.title(),
            param.about(),
            param.picture(),
            param.picture(),
            param.bio(),
            param.owner_id());
    }
}
