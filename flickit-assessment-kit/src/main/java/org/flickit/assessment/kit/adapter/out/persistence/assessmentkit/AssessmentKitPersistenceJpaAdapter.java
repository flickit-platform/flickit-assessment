package org.flickit.assessment.kit.adapter.out.persistence.assessmentkit;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.assessmentkit.*;
import org.flickit.assessment.data.jpa.kit.kitlanguage.KitLanguageJpaEntity;
import org.flickit.assessment.data.jpa.kit.kitlanguage.KitLanguageJpaRepository;
import org.flickit.assessment.data.jpa.kit.kittagrelation.KitTagRelationJpaEntity;
import org.flickit.assessment.data.jpa.kit.kittagrelation.KitTagRelationJpaRepository;
import org.flickit.assessment.data.jpa.kit.seq.KitDbSequenceGenerators;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaRepository;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.flickit.assessment.data.jpa.users.user.UserJpaRepository;
import org.flickit.assessment.kit.adapter.out.persistence.kittagrelation.KitTagRelationMapper;
import org.flickit.assessment.kit.adapter.out.persistence.users.expertgroup.ExpertGroupMapper;
import org.flickit.assessment.kit.adapter.out.persistence.users.user.UserMapper;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitMinimalInfoUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.*;
import org.flickit.assessment.kit.application.port.out.kituseraccess.DeleteKitUserAccessPort;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.flickit.assessment.kit.adapter.out.persistence.assessmentkit.AssessmentKitMapper.mapToDomainModel;
import static org.flickit.assessment.kit.adapter.out.persistence.assessmentkit.AssessmentKitMapper.mapToDomainModelWithMetadata;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@Component
@RequiredArgsConstructor
public class AssessmentKitPersistenceJpaAdapter implements
    LoadKitUsersPort,
    DeleteKitUserAccessPort,
    LoadKitMinimalInfoPort,
    CreateAssessmentKitPort,
    UpdateKitLastMajorModificationTimePort,
    CountKitStatsPort,
    UpdateKitInfoPort,
    LoadAssessmentKitPort,
    LoadPublishedKitListPort,
    CountKitListStatsPort,
    DeleteAssessmentKitPort,
    CountKitAssessmentsPort,
    LoadExpertGroupKitListPort,
    SearchKitOptionsPort,
    LoadActiveKitVersionIdPort,
    UpdateKitActiveVersionPort {

    private final AssessmentKitJpaRepository repository;
    private final UserJpaRepository userRepository;
    private final ExpertGroupJpaRepository expertGroupRepository;
    private final KitTagRelationJpaRepository kitTagRelationRepository;
    private final KitDbSequenceGenerators sequenceGenerators;
    private final KitLanguageJpaRepository kitLanguageRepository;

    @Override
    public PaginatedResponse<LoadKitUsersPort.KitUser> loadKitUsers(LoadKitUsersPort.Param param) {
        PageRequest pageRequest = PageRequest.of(
            param.page(),
            param.size(),
            Sort.by(Sort.Order.asc(UserJpaEntity.Fields.displayName))
        );

        Page<UserJpaEntity> pageResult = repository.findAllKitUsers(param.kitId(), pageRequest);

        List<LoadKitUsersPort.KitUser> users = pageResult.stream()
            .map(UserMapper::mapToUserListItem)
            .toList();

        return new PaginatedResponse<>(
            users,
            pageResult.getNumber(),
            pageResult.getSize(),
            UserJpaEntity.Fields.displayName,
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
                expertGroupEntity.getTitle()
            )
        );
    }

    @Override
    public Long persist(CreateAssessmentKitPort.Param param) {
        long kitId = sequenceGenerators.generateKitId();
        AssessmentKitJpaEntity kitEntity = AssessmentKitMapper.toJpaEntity(param);
        kitEntity.setId(kitId);
        repository.save(kitEntity);

        KitLanguageJpaEntity languageEntity = new KitLanguageJpaEntity(kitId, kitEntity.getLanguageId());
        kitLanguageRepository.save(languageEntity);
        return kitId;
    }

    @Override
    public void updateLastMajorModificationTime(Long kitId, LocalDateTime lastMajorModificationTime) {
        repository.updateLastMajorModificationTime(kitId, lastMajorModificationTime);
    }

    @Override
    public CountKitStatsPort.Result countKitStats(long kitId) {
        CountKitStatsView kitStats = repository.countKitStats(kitId);
        return new CountKitStatsPort.Result(kitStats.getQuestionnaireCount(),
            kitStats.getAttributeCount(),
            kitStats.getQuestionCount(),
            kitStats.getMaturityLevelCount(),
            kitStats.getLikeCount(),
            kitStats.getAssessmentCount(),
            kitStats.getMeasuresCount());
    }

    @Override
    public void update(UpdateKitInfoPort.Param param) {
        var kitEntity = repository.findById(param.kitId())
            .orElseThrow(() -> new ResourceNotFoundException(UPDATE_KIT_INFO_KIT_ID_NOT_FOUND));

        if (param.tags() != null)
            updateKitTags(param.kitId(), param.tags());

        var toBeUpdatedEntity = AssessmentKitMapper.toJpaEntity(kitEntity, param);
        repository.save(toBeUpdatedEntity);
    }

    private void updateKitTags(Long kitId, Set<Long> tags) {
        Set<Long> savedTags = kitTagRelationRepository.findAllByKitId(kitId).stream()
            .map(KitTagRelationJpaEntity::getTagId)
            .collect(Collectors.toSet());

        var removedTags = savedTags.stream()
            .filter(t -> !tags.contains(t))
            .collect(Collectors.toSet());

        var addedTags = tags.stream()
            .filter(t -> !savedTags.contains(t))
            .collect(Collectors.toSet());

        kitTagRelationRepository.saveAll(addedTags.stream()
            .map(tagId -> KitTagRelationMapper.toJpaEntity(tagId, kitId))
            .collect(Collectors.toSet()));

        kitTagRelationRepository.deleteByKitIdAndTagIdIn(kitId, removedTags);
    }

    @Override
    public AssessmentKit load(long kitId) {
        AssessmentKitJpaEntity kitEntity = repository.findById(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        return mapToDomainModel(kitEntity);
    }

    @Override
    public AssessmentKit loadTranslated(long kitId) {
        AssessmentKitJpaEntity kitEntity = repository.findById(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        var language = KitLanguage.valueOf(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        return mapToDomainModelWithMetadata(kitEntity, language);
    }

    @Override
    public PaginatedResponse<LoadPublishedKitListPort.Result> loadPublicKits(@Nullable Collection<KitLanguage> kitLanguages,
                                                                             int page,
                                                                             int size) {
        var kitLanguageIds = resolveKitLanguages(kitLanguages);
        var sort = Sort.by(Sort.Order.asc(AssessmentKitJpaEntity.Fields.title));
        var pageResult = repository.findAllPublishedAndNotPrivate(kitLanguageIds, PageRequest.of(page, size, sort));

        return toLoadPublishedKitsPortResult(pageResult);
    }

    @Override
    public PaginatedResponse<LoadPublishedKitListPort.Result> loadPublicKits(UUID userId,
                                                                             @Nullable Collection<KitLanguage> kitLanguages,
                                                                             int page,
                                                                             int size) {
        var kitLanguageIds = resolveKitLanguages(kitLanguages);
        var sort = Sort.by(Sort.Order.asc(AssessmentKitJpaEntity.Fields.title));
        var pageResult = repository.findAllPublishedAndNotPrivateByUserId(userId, kitLanguageIds, PageRequest.of(page, size, sort));

        return toLoadPublishedKitsPortResult(pageResult);
    }

    @Override
    public PaginatedResponse<LoadPublishedKitListPort.Result> loadPrivateKits(UUID userId,
                                                                              @Nullable
                                                                              Collection<KitLanguage> kitLanguages,
                                                                              int page,
                                                                              int size) {
        var kitLanguageIds = resolveKitLanguages(kitLanguages);
        var sort = Sort.by(Sort.Order.asc(AssessmentKitJpaEntity.Fields.title));
        var pageResult = repository.findAllPublishedAndPrivateByUserId(userId, kitLanguageIds, PageRequest.of(page, size, sort));

        return toLoadPublishedKitsPortResult(pageResult);
    }

    @Override
    public PaginatedResponse<LoadPublishedKitListPort.Result> loadPrivateAndPublicKits(UUID currentUserId,
                                                                                       @Nullable
                                                                                       Set<KitLanguage> kitLanguages,
                                                                                       int page,
                                                                                       int size) {
        var kitLanguageIds = resolveKitLanguages(kitLanguages);
        var sort = Sort.by(
            Sort.Order.desc(AssessmentKitJpaEntity.Fields.isPrivate),
            Sort.Order.asc(AssessmentKitJpaEntity.Fields.title));
        var pageResult = repository.findAllPublished(currentUserId, kitLanguageIds, PageRequest.of(page, size, sort));

        return toLoadPublishedKitsPortResult(pageResult);
    }

    @Nullable
    private Set<Integer> resolveKitLanguages(Collection<KitLanguage> languages) {
        if (isNotEmpty(languages))
            return languages.stream()
                .map(KitLanguage::getId)
                .collect(toSet());
        return null;
    }

    private PaginatedResponse<LoadPublishedKitListPort.Result> toLoadPublishedKitsPortResult(Page<KitWithExpertGroupView> pageResult) {
        var language = KitLanguage.valueOf(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        var items = pageResult.getContent().stream()
            .map(v -> new LoadPublishedKitListPort.Result(
                mapToDomainModel(v.getKit(), language),
                ExpertGroupMapper.mapToDomainModel(v.getExpertGroup()),
                v.getKitUserAccess()))
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            AssessmentKitJpaEntity.Fields.title,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    @Override
    public List<CountKitListStatsPort.Result> countKitsStats(List<Long> kitIds) {
        List<CountKitStatsView> kitsStats = repository.countKitStats(kitIds);
        return kitsStats.stream().map(x -> new CountKitListStatsPort.Result(x.getId(),
                x.getLikeCount(),
                x.getAssessmentCount()))
            .toList();
    }

    @Override
    public void delete(Long kitId) {
        repository.deleteById(kitId);
    }

    @Override
    public long count(Long kitId) {
        return repository.countAllKitAssessments(kitId);
    }

    @Override
    public PaginatedResponse<AssessmentKit> loadExpertGroupKits(long expertGroupId, UUID userId,
                                                                boolean includeUnpublishedKits, int page, int size) {
        var pageResult = repository.findExpertGroupKitsOrderByPublishedAndModificationTimeDesc(expertGroupId,
            userId,
            includeUnpublishedKits,
            KitVersionStatus.UPDATING.getId(),
            PageRequest.of(page, size));
        var items = pageResult.getContent().stream().map(AssessmentKitMapper::mapToDomainModel).toList();

        return new PaginatedResponse<>(items,
            pageResult.getNumber(),
            pageResult.getSize(),
            AssessmentKitJpaEntity.Fields.lastModificationTime,
            Sort.Direction.DESC.name().toLowerCase(),
            (int) pageResult.getTotalElements());
    }

    @Override
    public PaginatedResponse<AssessmentKit> searchKitOptions(SearchKitOptionsPort.Param param) {
        var query = param.query() == null ? "" : param.query();
        var specification = new AssessmentKitSearchSpecification(query, param.currentUserId());
        var kitEntityPage = repository.findAll(specification,
            PageRequest.of(param.page(), param.size(), Sort.Direction.ASC, AssessmentKitJpaEntity.Fields.title));

        var translationLanguage = KitLanguage.valueOf(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        var kits = kitEntityPage.getContent().stream()
            .map(entity -> AssessmentKitMapper.mapToDomainModel(entity, translationLanguage))
            .toList();

        return new PaginatedResponse<>(kits,
            kitEntityPage.getNumber(),
            kitEntityPage.getSize(),
            AssessmentKitJpaEntity.Fields.title,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) kitEntityPage.getTotalElements());
    }

    @Override
    public long loadKitVersionId(long kitId) {
        return repository.loadKitVersionId(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_VERSION_ID_NOT_FOUND));
    }

    @Override
    public void updateActiveVersion(long kitId, long activeVersionId) {
        repository.updateKitVersionId(kitId, activeVersionId);
    }
}
