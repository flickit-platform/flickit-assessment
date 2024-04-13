package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.KitTag;
import org.flickit.assessment.kit.application.port.in.assessmentkit.EditKitInfoUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UpdateKitInfoPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kittag.LoadKitTagsListPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class EditKitInfoService implements EditKitInfoUseCase {

    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final UpdateKitInfoPort updateKitInfoPort;
    private final LoadAssessmentKitPort loadKitPort;
    private final LoadKitTagsListPort loadKitTagsListPort;

    @Override
    public Result editKitInfo(Param param) {
        validateCurrentUser(param.getKitId(), param.getCurrentUserId());
        if (containsNonNullParam(param)) {
            return updateKitInfoPort.update(toPortParam(param));
        } else {
            var kit = loadKitPort.load(param.getKitId());
            var tags = loadKitTagsListPort.load(param.getKitId())
                .stream().map(t -> new KitTag(t.getId(), t.getTitle()))
                .toList();
            return toResult(kit, tags);
        }
    }

    private void validateCurrentUser(Long kitId, UUID currentUserId) {
        Long expertGroupId = loadKitExpertGroupPort.loadKitExpertGroup(kitId).getId();
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId);
        if (!Objects.equals(expertGroupOwnerId, currentUserId)) {
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        }
    }

    private boolean containsNonNullParam(Param param) {
        return Objects.nonNull(param.getTitle()) ||
            Objects.nonNull(param.getSummary()) ||
            Objects.nonNull(param.getIsActive()) ||
            Objects.nonNull(param.getIsPrivate()) ||
            Objects.nonNull(param.getPrice()) ||
            Objects.nonNull(param.getAbout()) ||
            Objects.nonNull(param.getTags());
    }

    private UpdateKitInfoPort.Param toPortParam(Param param) {
        return new UpdateKitInfoPort.Param(
            param.getKitId(),
            param.getTitle(),
            param.getSummary(),
            param.getIsActive(),
            param.getIsPrivate(),
            param.getPrice(),
            param.getAbout(),
            param.getTags(),
            param.getCurrentUserId(),
            LocalDateTime.now()
        );
    }

    private Result toResult(AssessmentKit kit, List<KitTag> tags) {
        return new Result(
            kit.getTitle(),
            kit.getSummary(),
            kit.isPublished(),
            kit.isPrivate(),
            0.0,
            kit.getAbout(),
            tags
        );
    }
}
