package org.flickit.assessment.kit.application.service.levelcompetence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.domain.MaturityLevelCompetence;
import org.flickit.assessment.kit.application.port.in.levelcompetence.GetLevelCompetencesUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetLevelCompetencesService implements GetLevelCompetencesUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Override
    public List<LevelWithCompetencesListItem> getLevelCompetences(Param param) {
        var kit = loadKitVersionPort.load(param.getKitVersionId()).getKit();
        if (!checkExpertGroupAccessPort.checkIsMember(kit.getExpertGroupId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        List<MaturityLevel> maturityLevels = loadMaturityLevelsPort.loadAllByKitVersionId(param.getKitVersionId());
        return maturityLevels.stream()
            .map(this::toResult)
            .toList();
    }

    private LevelWithCompetencesListItem toResult(MaturityLevel maturityLevel) {
        return new LevelWithCompetencesListItem(maturityLevel.getId(), maturityLevel.getIndex(), maturityLevel.getTitle(),
            maturityLevel.getCompetences().stream()
                .map(this::toCompetence)
                .toList());
    }

    private Competence toCompetence(MaturityLevelCompetence competence) {
        return new Competence(competence.getId(), competence.getValue(), competence.getEffectiveLevelId());
    }
}
