package org.flickit.assessment.kit.application.service.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.measure.UpdateMeasureUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.measure.UpdateMeasurePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.util.GenerateHashCodeUtil.generateCode;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateMeasureService implements UpdateMeasureUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final UpdateMeasurePort updateMeasurePort;

    @Override
    public void updateMeasure(Param param) {
        var kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        if (!loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId()).equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        updateMeasurePort.update(toParam(param));
    }

    UpdateMeasurePort.Param toParam(Param param) {
        return new UpdateMeasurePort.Param(param.getMeasureId(),
            param.getKitVersionId(),
            param.getTitle(),
            generateCode(param.getTitle()),
            param.getIndex(),
            param.getDescription(),
            param.getTranslations(),
            LocalDateTime.now(),
            param.getCurrentUserId());
    }
}
