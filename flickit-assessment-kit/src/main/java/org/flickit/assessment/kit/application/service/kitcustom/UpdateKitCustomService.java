package org.flickit.assessment.kit.application.service.kitcustom;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.util.SlugCodeUtil;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.KitCustomData;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitFullInfoPort;
import org.flickit.assessment.kit.application.port.in.kitcustom.UpdateKitCustomUseCase;
import org.flickit.assessment.kit.application.port.out.kitcustom.UpdateKitCustomPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.CheckKitUserAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateKitCustomService implements UpdateKitCustomUseCase {

    private final LoadAssessmentKitFullInfoPort loadAssessmentKitFullInfoPort;
    private final CheckKitUserAccessPort checkKitUserAccessPort;
    private final UpdateKitCustomPort updateKitCustomPort;

    @Override
    public void updateKitCustom(Param param) {
        AssessmentKit kit = loadAssessmentKitFullInfoPort.load(param.getKitId());
        if (kit.isPrivate() && !checkKitUserAccessPort.hasAccess(param.getKitId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        updateKitCustomPort.update(toParam(param));
    }

    private UpdateKitCustomPort.Param toParam(Param param) {
        String code = SlugCodeUtil.generateSlugCode(param.getTitle());
        KitCustomData kitCustomData = toKitCustomData(param.getCustomData());

        return new UpdateKitCustomPort.Param(param.getKitCustomId(),
            param.getKitId(),
            param.getTitle(),
            code,
            kitCustomData,
            LocalDateTime.now(),
            param.getCurrentUserId());
    }

    private KitCustomData toKitCustomData(UpdateKitCustomUseCase.Param.KitCustomData customData) {
        var subjects = customData.customSubjects().stream()
            .map(e -> new KitCustomData.Subject(e.getId(), e.getWeight()))
            .toList();

        var attributes = customData.customAttributes().stream()
            .map(e -> new KitCustomData.Attribute(e.getId(), e.getWeight()))
            .toList();

        return new KitCustomData(subjects, attributes);
    }
}
