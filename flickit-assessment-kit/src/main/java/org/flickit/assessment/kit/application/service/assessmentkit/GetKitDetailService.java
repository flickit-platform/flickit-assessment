package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDetailUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairesPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitDetailService implements GetKitDetailUseCase {

    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final LoadSubjectsPort loadSubjectsPort;
    private final LoadQuestionnairesPort loadQuestionnairesPort;

    @Override
    public Result getKitDetail(Param param) {
        ExpertGroup expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId());
        if (!checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var maturityLevels = loadMaturityLevelsPort.loadByKitId(param.getKitId());
        var subjects = loadSubjectsPort.loadSubjects(param.getKitId());
        var questionnaires = loadQuestionnairesPort.loadAllByKitId(param.getKitId());

        return mapToResult(maturityLevels, subjects, questionnaires);
    }

    private Result mapToResult(List<MaturityLevel> maturityLevels, List<Subject> subjects, List<Questionnaire> questionnaires) {
        var maturityLevelIdTitleMap = maturityLevels.stream()
            .collect(Collectors.toMap(MaturityLevel::getId, MaturityLevel::getTitle));
        var kitDetailMaturityLevels = maturityLevels.stream()
            .map(m -> new KitDetailMaturityLevel(m.getId(), m.getTitle(), m.getIndex(),
                m.getCompetences().stream()
                    .map(c -> {
                        long id = c.getEffectiveLevelId();
                        String title = maturityLevelIdTitleMap.get(id);
                        return new Competences(title, c.getValue(), id);
                    })
                    .toList()
            ))
            .toList();

        var kitDetailSubjects = subjects.stream()
            .map(s -> new KitDetailSubject(s.getId(), s.getTitle(), s.getIndex()))
            .toList();

        var kitDetailQuestionnaires = questionnaires.stream()
            .map(q -> new KitDetailQuestionnaire(q.getId(), q.getTitle(), q.getIndex()))
            .toList();

        return new Result(kitDetailMaturityLevels, kitDetailSubjects, kitDetailQuestionnaires);
    }
}
