package org.flickit.assessment.kit.application.service.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.Measure;
import org.flickit.assessment.kit.application.port.in.measure.CreateMeasureUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.measure.CreateMeasurePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.util.GenerateHashCodeUtil.generateCode;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateMeasureService implements CreateMeasureUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final CreateMeasurePort createMeasurePort;

    @Override
    public Result createMeasure(Param param) {
        KitVersion kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        long id = createMeasurePort.persist(toMeasure(param), param.getKitVersionId(), param.getCurrentUserId());
        return new Result(id);
    }

    private Measure toMeasure(Param param) {
        Measure measure = new Measure(null,
            generateCode(param.getTitle()),
            param.getTitle(),
            param.getIndex(),
            param.getDescription(),
            LocalDateTime.now(),
            LocalDateTime.now());
        measure.setTranslations(param.getTranslations());
        return measure;
    }
}
