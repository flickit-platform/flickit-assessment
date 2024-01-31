package org.flickit.assessment.kit.adapter.out.persistence.expertgroup;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaRepository;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupWithDetailsView;
import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupListUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.CreateExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.*;
import org.flickit.assessment.kit.config.MinioConfigProperties;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupMemberIdsPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.flickit.assessment.kit.adapter.out.persistence.expertgroup.ExpertGroupMapper.mapToPortResult;

@Component
@RequiredArgsConstructor
public class ExpertGroupPersistenceJpaAdapter implements
    LoadExpertGroupOwnerPort,
    LoadExpertGroupListPort,
    LoadExpertGroupMembersPort,
    LoadExpertGroupMembersPictureLinkPort,
    LoadExpertGroupMemberIdsPort,
    CreateExpertGroupPort {

    private final ExpertGroupJpaRepository repository;
    private final MinioClient minioClient;
    private final MinioConfigProperties properties;

    @Override
    public Optional<UUID> loadOwnerId(Long expertGroupId) {
        return Optional.of(repository.loadOwnerIdById(expertGroupId));
    }

    @Override
    public Long persist(CreateExpertGroupPort.Param param) {
        ExpertGroupJpaEntity unsavedEntity = ExpertGroupMapper.mapCreateParamToJpaEntity(param);
        ExpertGroupJpaEntity savedEntity = repository.save(unsavedEntity);
        return savedEntity.getId();
    }

    @Override
    public PaginatedResponse<LoadExpertGroupListPort.Result> loadExpertGroupList(LoadExpertGroupListPort.Param param) {
        var pageResult = repository.findByUserId(
            param.currentUserId(),
            PageRequest.of(param.page(), param.size()));

        List<LoadExpertGroupListPort.Result> items = pageResult.getContent().stream()
            .map(e -> resultWithMembers(e, param.sizeOfMembers()))
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            ExpertGroupJpaEntity.Fields.NAME,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    private LoadExpertGroupListPort.Result resultWithMembers(ExpertGroupWithDetailsView item, int membersCount) {
        var members = repository.findMembersByExpertGroupId(item.getId(),
                PageRequest.of(0, membersCount, Sort.Direction.ASC, UserJpaEntity.Fields.NAME))
            .stream()
            .map(GetExpertGroupListUseCase.Member::new)
            .toList();
        return mapToPortResult(item, members);
    }

    @Override
    public PaginatedResponse<LoadExpertGroupMembersPort.Result> loadExpertGroupMembers(LoadExpertGroupMembersPort.Param param) {
        var pageResult = repository.findExpertGroupMembers(param.expertGroupId(),
            PageRequest.of(param.page(), param.size(), Sort.Direction.ASC, UserJpaEntity.Fields.NAME));

        var items = pageResult
            .stream()
            .map(MembersMapper::mapToResult)
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            ExpertGroupJpaEntity.Fields.NAME,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    @SneakyThrows
    @Override
    public String loadMembersPictureLink(String filePath, Duration expiryDuration) {
        String bucketName = properties.getBucketName();

        if (!checkPictureExistence(filePath, bucketName)) return null;

        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
            .bucket(bucketName)
            .object(filePath)
            .expiry((int) expiryDuration.getSeconds(), TimeUnit.SECONDS)
            .method(Method.GET)
            .build());
    }

    @SneakyThrows
    private boolean checkPictureExistence(String path, String bucketName) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                .bucket(bucketName)
                .object(path)
                .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<LoadExpertGroupMemberIdsPort.Result> loadMemberIds(long expertGroupId) {
        List<UUID> memberIds = repository.findMemberIdsByExpertGroupId(expertGroupId);
        return memberIds.stream()
            .map(LoadExpertGroupMemberIdsPort.Result::new)
            .toList();
    }
}
