package org.flickit.assessment.kit.application.service.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.measure.GetMeasuresUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.measure.LoadMeasurePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetMeasuresService implements GetMeasuresUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadMeasurePort loadMeasurePort;

    @Override
    public PaginatedResponse<MeasureListItem> getMeasures(Param param) {
        KitVersion kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        if (!checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var pageResult = loadMeasurePort.loadAll(param.getKitVersionId(), param.getPage(), param.getSize());
        List<MeasureListItem> items = pageResult.getItems().stream()
            .map(e -> new MeasureListItem(e.measure(), e.questionsCount()))
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getPage(),
            pageResult.getSize(),
            pageResult.getSort(),
            pageResult.getOrder(),
            pageResult.getTotal()
        );
    }
}
