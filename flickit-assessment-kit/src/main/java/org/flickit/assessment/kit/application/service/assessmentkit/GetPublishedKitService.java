package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetPublishedKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CountKitStatsPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitFullInfoPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.assessmentkitaccess.CheckKitAccessPort;
import org.flickit.assessment.kit.application.port.out.kittag.LoadKitTagsListPort;
import org.flickit.assessment.kit.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetPublishedKitService implements GetPublishedKitUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofHours(1);

    private final LoadAssessmentKitFullInfoPort loadAssessmentKitFullInfoPort;
    private final CheckKitAccessPort checkKitAccessPort;
    private final CountKitStatsPort countKitStatsPort;
    private final LoadKitTagsListPort loadKitTagsListPort;
    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Override
    public Result getPublishedKit(Param param) {
        AssessmentKit kit = loadAssessmentKitFullInfoPort.load(param.getKitId());
        if (!kit.isPublished()) {
            throw new ResourceNotFoundException(KIT_ID_NOT_FOUND);
        }
        if (kit.isPrivate() && !checkKitAccessPort.checkHasAccess(param.getKitId(), param.getCurrentUserId())) {
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        }

        var stats = countKitStatsPort.countKitStats(param.getKitId());

        var subjects = kit.getSubjects().stream()
            .map(this::toSubject)
            .toList();

        var questionnaires = kit.getQuestionnaires().stream()
            .map(this::toQuestionnaire)
            .toList();

        var maturityLevels = kit.getMaturityLevels().stream()
            .map(this::toMaturityLevel)
            .toList();

        var kitTags = loadKitTagsListPort.load(param.getKitId()).stream()
            .map(this::toKitTag)
            .toList();

        var expertGroup = toExpertGroup(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId()));

        return new Result(
            kit.getId(),
            kit.getTitle(),
            kit.getSummary(),
            kit.getAbout(),
            kit.isPublished(),
            kit.isPrivate(),
            kit.getCreationTime(),
            kit.getLastModificationTime(),
            stats.likes(),
            stats.assessmentCounts(),
            subjects.size(),
            stats.questionnairesCount(),
            subjects,
            questionnaires,
            maturityLevels,
            kitTags,
            expertGroup);
    }

    private Subject toSubject(org.flickit.assessment.kit.application.domain.Subject s) {
        return new Subject(
            s.getId(),
            s.getTitle(),
            s.getDescription(),
            s.getAttributes().stream()
                .map(this::toAttribute)
                .toList()
        );
    }

    private Attribute toAttribute(org.flickit.assessment.kit.application.domain.Attribute attribute) {
        return new Attribute(attribute.getId(), attribute.getTitle(), attribute.getDescription());
    }

    private Questionnaire toQuestionnaire(org.flickit.assessment.kit.application.domain.Questionnaire questionnaire) {
        return new Questionnaire(questionnaire.getId(), questionnaire.getTitle(), questionnaire.getDescription());
    }

    private MaturityLevel toMaturityLevel(org.flickit.assessment.kit.application.domain.MaturityLevel level) {
        return new MaturityLevel(level.getId(), level.getTitle(), level.getValue(), level.getIndex());
    }

    private KitTag toKitTag(org.flickit.assessment.kit.application.domain.KitTag tag) {
        return new KitTag(tag.getId(), tag.getTitle());
    }

    private ExpertGroup toExpertGroup(org.flickit.assessment.kit.application.domain.ExpertGroup expertGroup) {
        return new ExpertGroup(expertGroup.getId(),
            expertGroup.getTitle(),
            expertGroup.getBio(),
            expertGroup.getAbout(),
            createFileDownloadLinkPort.createDownloadLink(expertGroup.getPicture(), EXPIRY_DURATION));
    }
}
