package org.flickit.assessment.kit.adapter.out.persistence.expertgroup;

import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaRepository;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupWithDetailsView;
import org.flickit.assessment.data.jpa.kit.expertgroup.MemberView;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupListUseCase;

import java.util.function.BiFunction;

public class ExpertGroupMapper {

    static BiFunction<ExpertGroupJpaRepository, GetExpertGroupListUseCase.ExpertGroupListItem, GetExpertGroupListUseCase.ExpertGroupListItem>
        mapMembers = (repository, item) -> {
        var members = repository.getMembersByExpert(item.expertGroupId())
            .stream()
            .map(ExpertGroupMapper::mapToMember)
            .toList();

        return new GetExpertGroupListUseCase.ExpertGroupListItem(
            item.expertGroupId(),
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

    private ExpertGroupMapper() {
    }

    public static ExpertGroup mapToDomainModel(ExpertGroupJpaEntity entity) {
        return new ExpertGroup(entity.getId());
    }

    public static GetExpertGroupListUseCase.ExpertGroupListItem mapToExpertGroupListItem(ExpertGroupWithDetailsView entity) {
        return new GetExpertGroupListUseCase.ExpertGroupListItem(
            entity.getExpertGroupId(),
            entity.getName(),
            entity.getBio(),
            entity.getPicture(),
            entity.getPublishedKitsCount(),
            entity.getMembersCount(),
            null,
            entity.getOwnerId(),
            entity.getEditable());
    }

    public static GetExpertGroupListUseCase.Member mapToMember(MemberView entity) {
        return new GetExpertGroupListUseCase.Member(
            entity.getDisplayName());
    }
}
