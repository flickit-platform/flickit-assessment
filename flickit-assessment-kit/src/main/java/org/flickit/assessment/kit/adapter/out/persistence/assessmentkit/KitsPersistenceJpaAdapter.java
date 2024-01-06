package org.flickit.assessment.kit.adapter.out.persistence.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaRepository;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaRepository;
import org.flickit.assessment.data.jpa.kit.tag.AssessmentKitTagJpaEntity;
import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;
import org.flickit.assessment.data.jpa.kit.user.UserJpaRepository;
import org.flickit.assessment.kit.adapter.out.persistence.expertgroup.ExpertGroupMapper;
import org.flickit.assessment.kit.adapter.out.persistence.tag.TagMapper;
import org.flickit.assessment.kit.adapter.out.persistence.user.UserMapper;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetAssessmentKitsListUseCase;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitMinimalInfoUseCase;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitUserListUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitsListPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitMinimalInfoPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitUsersPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.DeleteKitUserAccessPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.GrantUserAccessToKitPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@Component
@RequiredArgsConstructor
public class KitsPersistenceJpaAdapter implements
    GrantUserAccessToKitPort,
    LoadKitExpertGroupPort,
    LoadKitUsersPort,
    DeleteKitUserAccessPort,
    LoadKitMinimalInfoPort,
    LoadAssessmentKitsListPort {

    private final AssessmentKitJpaRepository repository;
    private final UserJpaRepository userRepository;
    private final ExpertGroupJpaRepository expertGroupRepository;
    private final AssessmentJpaRepository assessmentRepository;

    @Override
    public boolean grantUserAccess(Long kitId, UUID userId) {
        AssessmentKitJpaEntity assessmentKit = repository.findById(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(GRANT_USER_ACCESS_TO_KIT_KIT_ID_NOT_FOUND));
        UserJpaEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(GRANT_USER_ACCESS_TO_KIT_USER_ID_NOT_FOUND));

        boolean isNew = assessmentKit.getAccessGrantedUsers().add(user);
        repository.save(assessmentKit);

        return isNew;
    }

    @Override
    public Long loadKitExpertGroupId(Long kitId) {
        return repository.loadKitExpertGroupId(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_ID_NOT_FOUND));
    }

    @Override
    public PaginatedResponse<GetKitUserListUseCase.UserListItem> loadKitUsers(LoadKitUsersPort.Param param) {
        Page<UserJpaEntity> pageResult = repository.findAllKitUsers(
            param.kitId(),
            PageRequest.of(param.page(), param.size()));

        List<GetKitUserListUseCase.UserListItem> items = pageResult.getContent().stream()
            .map(UserMapper::mapToUserListItem)
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            UserJpaEntity.Fields.NAME,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );

    }

    @Override
    public void delete(DeleteKitUserAccessPort.Param param) {
        AssessmentKitJpaEntity assessmentKit = repository.findById(param.kitId())
            .orElseThrow(() -> new ResourceNotFoundException(DELETE_KIT_USER_ACCESS_KIT_ID_NOT_FOUND));
        UserJpaEntity user = userRepository.findById(param.userId())
            .orElseThrow(() -> new ResourceNotFoundException(DELETE_KIT_USER_ACCESS_USER_NOT_FOUND));

        assessmentKit.getAccessGrantedUsers().remove(user);
        repository.save(assessmentKit);
    }

    @Override
    public GetKitMinimalInfoUseCase.Result loadKitMinimalInfo(Long kitId) {
        AssessmentKitJpaEntity kitEntity = repository.findById(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_KIT_MINIMAL_INFO_KIT_ID_NOT_FOUND));

        ExpertGroupJpaEntity expertGroupEntity = expertGroupRepository.findById(kitEntity.getExpertGroupId())
            .orElseThrow(() -> new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));

        return new GetKitMinimalInfoUseCase.Result(
                kitEntity.getId(),
                kitEntity.getTitle(),
                new GetKitMinimalInfoUseCase.MinimalExpertGroup(
                        expertGroupEntity.getId(),
                        expertGroupEntity.getName()
                )
        );
    }

    @Override
    public PaginatedResponse<GetAssessmentKitsListUseCase.KitsListItem> loadKitsList(LoadAssessmentKitsListPort.Param param) {
        Page<AssessmentKitJpaEntity> pageResult;
        if (param.isPrivate()) {
            pageResult =
                repository.findCurrentUserActivePrivateKits(param.currentUserId(), PageRequest.of(param.page(), param.size()));
        } else {
            pageResult = repository.findAllActivePublicKits(PageRequest.of(param.page(), param.size()));
        }

        List<GetAssessmentKitsListUseCase.KitsListItem> items = pageResult.getContent().stream()
            .map((AssessmentKitJpaEntity e) -> {
                Set<AssessmentKitTagJpaEntity> tags = e.getTags();
                ExpertGroupJpaEntity expertGroupEntity = expertGroupRepository.findById(e.getExpertGroupId()).get();
                List<AssessmentJpaEntity> assessmentEntities = assessmentRepository.findAllByAssessmentKitId(e.getId());
                int likesNumber = e.getLikes().size();
                int numberOfAssessments = assessmentEntities.size();
                List<GetAssessmentKitsListUseCase.KitsListItemTag> kitsListItemTags = TagMapper.mapToKitListItemTags(tags);
                GetAssessmentKitsListUseCase.KitsListItemExpertGroup kitsListItemExpertGroup =
                    ExpertGroupMapper.mapToKitListItemExpertGroup(expertGroupEntity);
                return KitMapper.mapToKitListItem(e, kitsListItemTags, kitsListItemExpertGroup, likesNumber, numberOfAssessments);
            })
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            AssessmentKitJpaEntity.Fields.TITLE,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }
}
