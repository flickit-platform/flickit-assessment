package org.flickit.assessment.kit.adapter.out.persistence.expertgroup;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaRepository;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupWithDetailsView;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupListPort.Result;

import java.util.function.BiFunction;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpertGroupMapper {

    static BiFunction<ExpertGroupJpaRepository, Result, Result>
        mapMembers = (repository, item) -> {

        var members = repository.findMembersByExpertId(item.id())
            .stream()
            .toList();

        return new Result(
            item.id(),
            item.title(),
            item.bio(),
            item.picture(),
            item.publishedKitsCount(),
            item.membersCount(),
            members,
            item.ownerId()
        );
    };

    public static Result mapToExpertGroupListItem(ExpertGroupWithDetailsView entity) {

        return new Result(
            entity.getId(),
            entity.getName(),
            entity.getBio(),
            entity.getPicture(),
            entity.getPublishedKitsCount(),
            entity.getMembersCount(),
            null,
            entity.getOwnerId());
    }
}
