package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitMetadata;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UpdateKitInfoUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UpdateKitInfoPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateKitInfoService implements UpdateKitInfoUseCase {

    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final UpdateKitInfoPort updateKitInfoPort;
    private final LoadAssessmentKitPort loadAssessmentKitPort;

    @Override
    public void updateKitInfo(Param param) {
        validateCurrentUser(param.getKitId(), param.getCurrentUserId());

        var kit = loadAssessmentKitPort.load(param.getKitId());
        var newMetadata = toDomainModel(param.getMetadata());

        KitMetadata existedMetadata = null;
        if (kit.getMetadata() != null)
            existedMetadata = kit.getMetadata();

        var metadata = buildMetadata(existedMetadata, newMetadata);
        if (containsNonNullParam(param) || param.isRemoveTranslations() || param.isRemoveMetadata())
            updateKitInfoPort.update(toPortParam(param, metadata));
    }

    private void validateCurrentUser(Long kitId, UUID currentUserId) {
        var expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(kitId);
        if (!Objects.equals(expertGroup.getOwnerId(), currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private boolean containsNonNullParam(Param param) {
        return Objects.nonNull(param.getTitle()) ||
            Objects.nonNull(param.getSummary()) ||
            Objects.nonNull(param.getLang()) ||
            Objects.nonNull(param.getPublished()) ||
            Objects.nonNull(param.getIsPrivate()) ||
            Objects.nonNull(param.getPrice()) ||
            Objects.nonNull(param.getAbout()) ||
            Objects.nonNull(param.getTags()) ||
            Objects.nonNull(param.getTranslations()) ||
            Objects.nonNull(param.getMetadata().getGoal()) ||
            Objects.nonNull(param.getMetadata().getContext());
    }

    private UpdateKitInfoPort.Param toPortParam(Param param, KitMetadata metadata) {
        return new UpdateKitInfoPort.Param(
            param.getKitId(),
            param.getTitle() != null ? generateSlugCode(param.getTitle()) : null,
            param.getTitle(),
            param.getSummary(),
            param.getLang() != null ? KitLanguage.valueOf(param.getLang()) : null,
            param.getPublished(),
            param.getIsPrivate(),
            param.getPrice(),
            param.getAbout(),
            param.getTags() != null ? new HashSet<>(param.getTags()) : null,
            param.getTranslations(),
            param.isRemoveTranslations(),
            metadata,
            param.isRemoveMetadata(),
            param.getCurrentUserId(),
            LocalDateTime.now()
        );
    }

    private KitMetadata buildMetadata(KitMetadata existedMetadata, KitMetadata newMetadata) {
        if (existedMetadata == null)
            return newMetadata;

        return new KitMetadata(
            resolveField(existedMetadata.goal(), newMetadata.goal()),
            resolveField(existedMetadata.context(), newMetadata.context())
        );
    }

    private <T> T resolveField(T existingValue, T newValue) {
        return newValue != null ? newValue : existingValue;
    }

    KitMetadata toDomainModel(MetadataParam metadata) {
        return new KitMetadata(metadata.getGoal(), metadata.getContext());
    }
}
