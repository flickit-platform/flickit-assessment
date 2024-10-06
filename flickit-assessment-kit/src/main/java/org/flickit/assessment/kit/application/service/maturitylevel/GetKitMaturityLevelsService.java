package org.flickit.assessment.kit.application.service.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.domain.MaturityLevelCompetence;
import org.flickit.assessment.kit.application.port.in.maturitylevel.GetKitMaturityLevelsUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitVersionExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitMaturityLevelsService implements GetKitMaturityLevelsUseCase {

    private final LoadKitVersionExpertGroupPort loadKitVersionExpertGroupPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Override
    public PaginatedResponse<MaturityLevelListItem> getKitMaturityLevels(Param param) {
        var expertGroup = loadKitVersionExpertGroupPort.loadKitVersionExpertGroup(param.getKitVersionId());
        if (!checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        var maturityLevels = loadMaturityLevelsPort.loadByKitVersionId(param.getKitVersionId(), param.getSize(), param.getPage());

        return new PaginatedResponse<>(
            mapToListItems(maturityLevels),
            maturityLevels.getPage(),
            maturityLevels.getSize(),
            maturityLevels.getSort(),
            maturityLevels.getOrder(),
            maturityLevels.getTotal());
    }

    private List<MaturityLevelListItem> mapToListItems(PaginatedResponse<MaturityLevel> maturityLevels) {
        return maturityLevels.getItems().stream()
            .map(x -> {
                var competences = x.getCompetences().stream()
                    .map(this::toCompetence)
                    .toList();
                return new MaturityLevelListItem(x.getId(), x.getTitle(), x.getIndex(), x.getValue(), competences);
            }).toList();
    }

    private Competences toCompetence(MaturityLevelCompetence competence) {
        return new Competences(competence.getId(),
            competence.getEffectiveLevelTitle(),
            competence.getValue(),
            competence.getEffectiveLevelId());
    }
}
