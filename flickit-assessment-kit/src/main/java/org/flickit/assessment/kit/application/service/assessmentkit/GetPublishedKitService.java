package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetPublishedKitUseCase;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetPublishedKitUseCase.Result.Language;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CountKitStatsPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.kitlanguage.LoadKitLanguagesPort;
import org.flickit.assessment.kit.application.port.out.kitlike.CheckKitLikeExistencePort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.CheckKitUserAccessPort;
import org.flickit.assessment.kit.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetPublishedKitService implements GetPublishedKitUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final LoadAssessmentKitPort loadAssessmentKitPort;
    private final CheckKitUserAccessPort checkKitUserAccessPort;
    private final CountKitStatsPort countKitStatsPort;
    private final LoadSubjectsPort loadSubjectsPort;
    private final CheckKitLikeExistencePort checkKitLikeExistencePort;
    private final LoadKitLanguagesPort loadKitLanguagesPort;
    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Override
    public Result getPublishedKit(Param param) {
        AssessmentKit kit = loadAssessmentKitPort.loadTranslated(param.getKitId());
        if (!kit.isPublished())
            throw new ResourceNotFoundException(KIT_ID_NOT_FOUND);

        boolean hasKitAccess = param.getCurrentUserId() != null &&
            checkKitUserAccessPort.hasAccess(param.getKitId(), param.getCurrentUserId());
        checkAccess(kit, hasKitAccess);

        var stats = countKitStatsPort.countKitStats(param.getKitId());

        var subjects = loadSubjectsPort.loadAllTranslated(kit.getActiveVersionId()).stream()
            .map(this::toSubject)
            .toList();

        var languages = loadKitLanguagesPort.loadByKitId(param.getKitId()).stream()
            .map(this::toLanguage)
            .toList();

        var goal = kit.getMetadata() != null ? kit.getMetadata().goal() : null;
        var context = kit.getMetadata() != null ? kit.getMetadata().context() : null;

        var expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId());

        return new Result(
            kit.getId(),
            kit.getTitle(),
            kit.getSummary(),
            kit.getAbout(),
            kit.isPublished(),
            kit.isPrivate(),
            kit.getCreationTime(),
            kit.getLastModificationTime(),
            new Like(stats.likes(), checkKitLikeExistencePort.exist(param.getKitId(), param.getCurrentUserId())),
            subjects.size(),
            kit.getExpertGroupId(),
            subjects,
            new Metadata(goal, context),
            languages,
            new ExpertGroup(expertGroup.getId(),
                expertGroup.getTitle(),
                getPictureDownloadLink(expertGroup.getPicture())),
            kit.getPrice() == 0,
            (!kit.isPrivate() && kit.getPrice() == 0) || hasKitAccess);
    }

    private void checkAccess(AssessmentKit kit, boolean hasKitAccess) {
        if (kit.isPrivate() && !hasKitAccess)
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private MinimalSubject toSubject(Subject s) {
        return new MinimalSubject(
            s.getId(),
            s.getTitle(),
            s.getDescription(),
            s.getAttributes().stream()
                .map(this::toAttribute)
                .toList()
        );
    }

    private MinimalAttribute toAttribute(Attribute attribute) {
        return new MinimalAttribute(attribute.getId(), attribute.getTitle(), attribute.getDescription());
    }

    private Language toLanguage(KitLanguage kitLanguage) {
        return new Language(kitLanguage.getCode(), kitLanguage.getTitle());
    }

    private String getPictureDownloadLink(String filePath) {
        if (isBlank(filePath))
            return null;
        return createFileDownloadLinkPort.createDownloadLink(filePath, EXPIRY_DURATION);
    }
}
