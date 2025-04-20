package org.flickit.assessment.kit.application.service.kitlanguage;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.port.in.kitlanguage.AddLanguageToKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitlanguage.CreateKitLanguagePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class AddLanguageToKitService implements AddLanguageToKitUseCase {

    private final LoadAssessmentKitPort loadAssessmentKitPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final CreateKitLanguagePort createKitLanguagePort;

    @Override
    public void addLanguageToKit(Param param) {
        AssessmentKit kit = loadAssessmentKitPort.load(param.getKitId());
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId());
        if (!param.getCurrentUserId().equals(ownerId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        createKitLanguagePort.persist(param.getKitId(), KitLanguage.valueOf(param.getLang()).getId());
    }
}
