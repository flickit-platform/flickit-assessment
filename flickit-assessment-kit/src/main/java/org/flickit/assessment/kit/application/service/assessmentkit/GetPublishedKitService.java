package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetPublishedKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CountKitStatsPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.kitlike.CheckKitLikeExistencePort;
import org.flickit.assessment.kit.application.port.out.kittag.LoadKitTagListPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.CheckKitUserAccessPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairesPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetPublishedKitService implements GetPublishedKitUseCase {

    private final LoadAssessmentKitPort loadAssessmentKitPort;
    private final CheckKitUserAccessPort checkKitUserAccessPort;
    private final CountKitStatsPort countKitStatsPort;
    private final LoadSubjectsPort loadSubjectsPort;
    private final LoadQuestionnairesPort loadQuestionnairesPort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final LoadKitTagListPort loadKitTagListPort;
    private final CheckKitLikeExistencePort checkKitLikeExistencePort;

    @Override
    public Result getPublishedKit(Param param) {
        AssessmentKit kit = loadAssessmentKitPort.load(param.getKitId());
        if (!kit.isPublished())
            throw new ResourceNotFoundException(KIT_ID_NOT_FOUND);

        if (kit.isPrivate() && !checkKitUserAccessPort.hasAccess(param.getKitId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var stats = countKitStatsPort.countKitStats(param.getKitId());

        var subjects = loadSubjectsPort.loadByKitVersionId(kit.getKitVersionId()).stream()
            .map(this::toSubject)
            .toList();

        var questionnaires = loadQuestionnairesPort.loadByKitId(param.getKitId()).stream()
            .map(this::toQuestionnaire)
            .toList();

        var maturityLevels = loadMaturityLevelsPort.loadByKitVersionId(kit.getKitVersionId()).stream()
            .map(this::toMaturityLevel)
            .toList();

        var kitTags = loadKitTagListPort.loadByKitId(param.getKitId()).stream()
            .map(this::toKitTag)
            .toList();

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
            stats.assessmentCounts(),
            subjects.size(),
            stats.questionnairesCount(),
            kit.getExpertGroupId(),
            subjects,
            questionnaires,
            maturityLevels,
            kitTags);
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

    private MinimalQuestionnaire toQuestionnaire(Questionnaire questionnaire) {
        return new MinimalQuestionnaire(questionnaire.getId(), questionnaire.getTitle(), questionnaire.getDescription());
    }

    private MinimalMaturityLevel toMaturityLevel(MaturityLevel level) {
        return new MinimalMaturityLevel(level.getId(), level.getTitle(), level.getDescription(), level.getValue(), level.getIndex());
    }

    private MinimalKitTag toKitTag(KitTag tag) {
        return new MinimalKitTag(tag.getId(), tag.getTitle());
    }
}
