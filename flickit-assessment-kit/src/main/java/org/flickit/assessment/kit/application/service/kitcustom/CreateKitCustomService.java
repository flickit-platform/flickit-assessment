package org.flickit.assessment.kit.application.service.kitcustom;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.kitcustom.KitCustomData;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.util.SlugCodeUtil;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.port.in.kitcustom.CreateKitCustomUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.kitcustom.CreateKitCustomPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.CheckKitUserAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateKitCustomService implements CreateKitCustomUseCase {

    private final CreateKitCustomPort createKitCustomPort;
    private final CheckKitUserAccessPort checkKitUserAccessPort;
    private final LoadAssessmentKitPort loadAssessmentKitPort;

    @Override
    public long createKitCustom(Param param) {
        AssessmentKit kit = loadAssessmentKitPort.load(param.getKitId());
        if (kit.isPrivate() && !checkKitUserAccessPort.hasAccess(param.getKitId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        return createKitCustomPort.persist(toParam(param));
    }

    private CreateKitCustomPort.Param toParam(Param param) {
        String code = SlugCodeUtil.generateSlugCode(param.getTitle());
        KitCustomData kitCustomData = toKitCustomData(param.getCustomData());

        return new CreateKitCustomPort.Param(param.getKitId(),
            param.getTitle(),
            code,
            kitCustomData,
            LocalDateTime.now(),
            param.getCurrentUserId());
    }

    private KitCustomData toKitCustomData(Param.KitCustomData customData) {
        var subjects = customData.customSubjects().stream()
            .map(e -> new KitCustomData.Subject(e.getId(), e.getWeight()))
            .toList();

        var attributes = customData.customAttributes().stream()
            .map(e -> new KitCustomData.Attribute(e.getId(), e.getWeight()))
            .toList();

        return new KitCustomData(subjects, attributes);
    }
}
