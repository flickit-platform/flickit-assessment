package org.flickit.assessment.kit.application.service.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.port.in.maturitylevel.GetKitMaturityLevelsUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitVersionExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitMaturityLevelsService implements GetKitMaturityLevelsUseCase {

    private final LoadKitVersionExpertGroupPort loadKitVersionExpertGroupPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Override
    public PaginatedResponse<Result> getKitMaturityLevels(Param param) {
        ExpertGroup expertGroup = loadKitVersionExpertGroupPort.loadKitVersionExpertGroup(param.getKitVersionId());
        if (!checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        var maturityLevels = loadMaturityLevelsPort.loadByKitVersionId(param.getKitVersionId(), param.getSize(), param.getPage());
        return mapToResult(maturityLevels);
    }

    private PaginatedResponse<Result> mapToResult(PaginatedResponse<MaturityLevel> maturityLevels) {
        var items = maturityLevels.getItems();
        var idToLevelTitleMap = items.stream().collect(Collectors.toMap(MaturityLevel::getId, MaturityLevel::getTitle));

        List<Result> result = items.stream()
            .map(x -> {
                List<Competences> competences = x.getCompetences().stream().map(c -> new Competences(c.getId(),
                        idToLevelTitleMap.get(c.getEffectiveLevelId()),
                        c.getValue(),
                        c.getEffectiveLevelId()))
                    .toList();
                return new Result(x.getId(), x.getTitle(), x.getIndex(), x.getValue(), competences);
            }).toList();

        return new PaginatedResponse<>(
            result,
            maturityLevels.getPage(),
            maturityLevels.getSize(),
            maturityLevels.getSort(),
            maturityLevels.getOrder(),
            maturityLevels.getTotal());
    }
}
