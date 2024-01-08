package org.flickit.assessment.kit.adapter.out.persistence.expertgroup;

import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaRepository;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupWithDetailsView;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupListUseCase;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupListUseCase.ExpertGroupListItem;

import java.util.function.BiFunction;

@NoArgsConstructor
public class ExpertGroupMapper {

    static BiFunction<ExpertGroupJpaRepository, ExpertGroupListItem, ExpertGroupListItem>
        mapMembers = (repository, item) -> {

        var members = repository.findMembersByExpertId(item.id())
            .stream()
            .toList();

        return new GetExpertGroupListUseCase.ExpertGroupListItem(
            item.id(),
            item.title(),
            item.bio(),
            item.picture(),
            item.publishedKitsCount(),
            item.membersCount(),
            members,
            item.ownerId(),
            item.editable()
        );
    };

    public static ExpertGroupListItem mapToExpertGroupListItem(ExpertGroupWithDetailsView entity) {

        return new ExpertGroupListItem(
            entity.getId(),
            entity.getName(),
            entity.getBio(),
            entity.getPicture(),
            entity.getPublishedKitsCount(),
            entity.getMembersCount(),
            null,
            entity.getOwnerId(),
            entity.getEditable());
    }
}
