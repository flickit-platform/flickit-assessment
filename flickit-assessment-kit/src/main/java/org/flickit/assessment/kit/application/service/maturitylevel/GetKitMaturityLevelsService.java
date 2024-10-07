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

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
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
        var paginatedResponse = loadMaturityLevelsPort.loadByKitVersionId(param.getKitVersionId(), param.getSize(), param.getPage());
        var items = mapToListItems(param.getKitVersionId(), paginatedResponse);
        return new PaginatedResponse<>(items,
            paginatedResponse.getPage(),
            paginatedResponse.getSize(),
            paginatedResponse.getSort(),
            paginatedResponse.getOrder(),
            paginatedResponse.getTotal());
    }

    private List<MaturityLevelListItem> mapToListItems(Long kitVersionId, PaginatedResponse<MaturityLevel> maturityLevels) {
        var effectiveLevelsId = maturityLevels.getItems().stream()
            .flatMap(x -> x.getCompetences().stream())
            .map(MaturityLevelCompetence::getEffectiveLevelId)
            .collect(toSet());

        var idToTitleMap = loadMaturityLevelsPort.loadByKitVersionId(kitVersionId, effectiveLevelsId)
            .stream().collect(toMap(MaturityLevel::getId, MaturityLevel::getTitle));

        return maturityLevels.getItems().stream()
            .map(x -> {
                var competences = x.getCompetences().stream()
                    .map(c -> toCompetence(c, idToTitleMap.get(c.getEffectiveLevelId())))
                    .toList();
                return new MaturityLevelListItem(x.getId(), x.getTitle(), x.getIndex(), x.getValue(), competences);
            }).toList();
    }

    private Competences toCompetence(MaturityLevelCompetence competence, String title) {
        return new Competences(competence.getId(),
            title,
            competence.getValue(),
            competence.getEffectiveLevelId());
    }
}
