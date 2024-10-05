package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDetailUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadActiveKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadAllMaturityLevelsPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairesPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingLong;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitDetailService implements GetKitDetailUseCase {

    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadAllMaturityLevelsPort loadAllMaturityLevelsPort;
    private final LoadSubjectsPort loadSubjectsPort;
    private final LoadQuestionnairesPort loadQuestionnairesPort;
    private final LoadActiveKitVersionIdPort loadActiveKitVersionIdPort;

    @Override
    public Result getKitDetail(Param param) {
        ExpertGroup expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId());
        if (!checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        var kitVersionId = loadActiveKitVersionIdPort.loadKitVersionId(param.getKitId());

        var maturityLevels = loadAllMaturityLevelsPort.loadByKitVersionId(kitVersionId);
        var subjects = loadSubjectsPort.loadByKitVersionId(kitVersionId);
        var questionnaires = loadQuestionnairesPort.loadByKitId(param.getKitId());

        return mapToResult(maturityLevels, subjects, questionnaires);
    }

    private Result mapToResult(List<MaturityLevel> maturityLevels, List<Subject> subjects, List<Questionnaire> questionnaires) {
        var maturityLevelIdMap = maturityLevels.stream()
            .collect(Collectors.toMap(MaturityLevel::getId, Function.identity()));

        var kitDetailMaturityLevels = maturityLevels.stream()
            .map(maturityLevel -> toKitDetailMaturityLevel(maturityLevel, maturityLevelIdMap))
            .toList();

        var kitDetailSubjects = subjects.stream()
            .map(s -> new KitDetailSubject(s.getId(), s.getTitle(), s.getIndex()))
            .toList();

        var kitDetailQuestionnaires = questionnaires.stream()
            .map(q -> new KitDetailQuestionnaire(q.getId(), q.getTitle(), q.getIndex()))
            .toList();

        return new Result(kitDetailMaturityLevels, kitDetailSubjects, kitDetailQuestionnaires);
    }

    private KitDetailMaturityLevel toKitDetailMaturityLevel(MaturityLevel maturityLevel, Map<Long, MaturityLevel> maturityLevelIdMap) {
        var competences = maturityLevel.getCompetences().stream()
            .sorted(comparingLong(MaturityLevelCompetence::getValue)) // sort by value
            .map(c -> {
                long id = c.getEffectiveLevelId();
                return new Competences(maturityLevelIdMap.get(id).getTitle(), c.getValue(), id);
            })
            .toList();
        return new KitDetailMaturityLevel(maturityLevel.getId(),
            maturityLevel.getTitle(),
            maturityLevel.getIndex(),
            competences);
    }
}
