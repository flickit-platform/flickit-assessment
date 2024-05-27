package org.flickit.assessment.users.adapter.out.persistence.expertgroup;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupWithDetailsView;
import org.flickit.assessment.data.jpa.users.expertgroup.KitsCountView;
import org.flickit.assessment.users.application.domain.ExpertGroup;
import org.flickit.assessment.users.application.port.in.expertgroup.GetExpertGroupListUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.CountExpertGroupKitsPort;
import org.flickit.assessment.users.application.port.out.expertgroup.CreateExpertGroupPort;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupListPort;

import java.time.LocalDateTime;
import java.util.List;

import static org.flickit.assessment.users.application.service.constant.ExpertGroupConstants.NOT_DELETED_DELETION_TIME;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpertGroupMapper {

    public static LoadExpertGroupListPort.Result mapToPortResult(ExpertGroupWithDetailsView entity,
                                                                 List<GetExpertGroupListUseCase.Member> recentMembers,
                                                                 int membersCount) {
        return new LoadExpertGroupListPort.Result(
            entity.getId(),
            entity.getTitle(),
            entity.getBio(),
            entity.getPicture(),
            entity.getPublishedKitsCount(),
            membersCount,
            recentMembers,
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

    static ExpertGroupJpaEntity mapCreateParamToJpaEntity(CreateExpertGroupPort.Param param) {
        LocalDateTime creationTime = LocalDateTime.now();
        return new ExpertGroupJpaEntity(
            null,
            param.code(),
            param.title(),
            param.bio(),
            param.about(),
            param.picture(),
            param.website(),
            param.currentUserId(),
            param.currentUserId(),
            param.currentUserId(),
            creationTime,
            creationTime,
            false,
            NOT_DELETED_DELETION_TIME
        );
    }

    public static CountExpertGroupKitsPort.Result mapKitsCountToPortResult(KitsCountView entity) {
        return new CountExpertGroupKitsPort.Result(
            entity.getPublishedKitsCount(),
            entity.getUnPublishedKitsCount());
    }
}
