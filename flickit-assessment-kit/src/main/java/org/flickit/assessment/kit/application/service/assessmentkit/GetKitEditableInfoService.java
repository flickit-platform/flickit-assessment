package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitMetadata;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitEditableInfoUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitlanguage.LoadKitLanguagesPort;
import org.flickit.assessment.kit.application.port.out.kittag.LoadKitTagListPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitEditableInfoService implements GetKitEditableInfoUseCase {

    private final LoadAssessmentKitPort loadAssessmentKitPort;
    private final LoadKitTagListPort loadKitTagListPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadKitLanguagesPort loadKitLanguagesPort;
    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final LoadKitVersionPort loadKitVersionPort;

    @Override
    public KitEditableInfo getKitEditableInfo(Param param) {
        var expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId());
        if (!checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentKit = loadAssessmentKitPort.load(param.getKitId());
        var draftVersionId = loadKitVersionPort.loadKitVersionIdWithUpdatingStatus(param.getKitId())
            .orElse(null);

        var tags = loadKitTagListPort.loadByKitId(param.getKitId());
        var languages = loadKitLanguagesPort.loadByKitId(param.getKitId()).stream()
            .map(this::toLanguage)
            .toList();
        var kitMetadata = assessmentKit.getMetadata() != null
            ? assessmentKit.getMetadata()
            : null;

        return new KitEditableInfo(
            assessmentKit.getId(),
            assessmentKit.getTitle(),
            assessmentKit.getSummary(),
            toLanguage(assessmentKit.getLanguage()),
            assessmentKit.isPublished(),
            assessmentKit.isPrivate(),
            0D,
            assessmentKit.getAbout(),
            draftVersionId,
            tags,
            expertGroup.getOwnerId().equals(param.getCurrentUserId()),
            assessmentKit.getActiveVersionId() != null,
            assessmentKit.getTranslations(),
            languages,
            toMetadata(kitMetadata));
    }

    private KitEditableInfo.Language toLanguage(KitLanguage kitLanguage) {
        return new KitEditableInfo.Language(kitLanguage.getCode(), kitLanguage.getTitle());
    }

    private KitEditableInfo.Metadata toMetadata(KitMetadata metadata) {
        return  (metadata == null)
            ? null
            : new KitEditableInfo.Metadata(metadata.goal(), metadata.context());
    }
}
