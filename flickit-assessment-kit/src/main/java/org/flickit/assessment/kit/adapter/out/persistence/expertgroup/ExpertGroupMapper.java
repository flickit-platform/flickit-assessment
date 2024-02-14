package org.flickit.assessment.kit.adapter.out.persistence.expertgroup;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupWithDetailsView;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupListUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.CreateExpertGroupPort.Param;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupListPort;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpertGroupMapper {

    public static LoadExpertGroupListPort.Result mapToPortResult(ExpertGroupWithDetailsView entity, List<GetExpertGroupListUseCase.Member> members) {
        return new LoadExpertGroupListPort.Result(
            entity.getId(),
            entity.getTitle(),
            entity.getBio(),
            entity.getPicture(),
            entity.getPublishedKitsCount(),
            entity.getMembersCount(),
            members,
            entity.getOwnerId());
    }

    public static ExpertGroup mapToDomainModel(ExpertGroupJpaEntity entity) {
        return new ExpertGroup(
            entity.getId(),
            entity.getTitle(),
            entity.getBio(),
            entity.getAbout(),
            entity.getPicture(),
            entity.getWebsite(),
            entity.getOwnerId()
        );
    }

    static ExpertGroupJpaEntity mapCreateParamToJpaEntity(Param param) {
        return new ExpertGroupJpaEntity(
            null,
            param.title(),
            param.bio(),
            param.about(),
            param.picture(),
            param.website(),
            param.currentUserId()
        );
    }
}
