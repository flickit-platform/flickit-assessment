package org.flickit.assessment.kit.application.service.kitcustom;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.common.util.SlugCodeUtil;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.KitCustomData;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.port.in.kitcustom.CreateKitCustomUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitFullInfoPort;
import org.flickit.assessment.kit.application.port.out.kitcustom.CreateKitCustomPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.CheckKitUserAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.CREATE_KIT_CUSTOM_UNRELATED_ATTRIBUTE_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.CREATE_KIT_CUSTOM_UNRELATED_SUBJECT_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateKitCustomService implements CreateKitCustomUseCase {

    private final CreateKitCustomPort createKitCustomPort;
    private final CheckKitUserAccessPort checkKitUserAccessPort;
    private final LoadAssessmentKitFullInfoPort loadAssessmentKitFullInfoPort;

    @Override
    public long createKitCustom(Param param) {
        AssessmentKit kit = loadAssessmentKitFullInfoPort.load(param.getKitId());
        if (kit.isPrivate() && !checkKitUserAccessPort.hasAccess(param.getKitId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var outPortParam = toParam(param);
        KitCustomData customData = outPortParam.customData();

        if (customData.subjects() != null) {
            Set<Long> ids = customData.subjects().stream()
                .map(KitCustomData.Subject::id)
                .collect(Collectors.toSet());
            Set<Long> kitSubjectIds = kit.getSubjects().stream()
                .map(Subject::getId)
                .collect(Collectors.toSet());
            if (!kitSubjectIds.containsAll(ids))
                throw new ValidationException(CREATE_KIT_CUSTOM_UNRELATED_SUBJECT_NOT_ALLOWED);
        }

        if (customData.attributes() != null) {
            Set<Long> ids = customData.attributes().stream()
                .map(KitCustomData.Attribute::id)
                .collect(Collectors.toSet());
            Set<Long> kitAttributeIds = kit.getSubjects().stream()
                .map(Subject::getAttributes)
                .flatMap(Collection::stream)
                .map(Attribute::getId)
                .collect(Collectors.toSet());
            if (!kitAttributeIds.containsAll(ids))
                throw new ValidationException(CREATE_KIT_CUSTOM_UNRELATED_ATTRIBUTE_NOT_ALLOWED);
        }

        return createKitCustomPort.persist(outPortParam);
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
