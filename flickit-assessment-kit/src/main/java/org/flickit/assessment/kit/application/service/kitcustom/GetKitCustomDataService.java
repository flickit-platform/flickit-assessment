package org.flickit.assessment.kit.application.service.kitcustom;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.KitCustomData;
import org.flickit.assessment.kit.application.port.in.kitcustom.GetKitCustomDataUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.kitcustom.LoadKitCustomPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.CheckKitUserAccessPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_CUSTOM_DATA_KIT_CUSTOM_ID_INVALID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitCustomDataService implements GetKitCustomDataUseCase {

    private final LoadSubjectPort loadSubjectPort;
    private final LoadAssessmentKitPort loadAssessmentKitPort;
    private final CheckKitUserAccessPort checkKitUserAccessPort;
    private final LoadKitCustomPort loadKitCustomPort;

    @Override
    public PaginatedResponse<Result> getKitCustomData(Param param) {
        AssessmentKit kit = loadAssessmentKitPort.load(param.getKitId());
        if (kit.isPrivate() && !checkKitUserAccessPort.hasAccess(param.getKitId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var kitCustom = loadKitCustomPort.loadById(param.getKitCustomId());
        if (kitCustom.kitId() != kit.getId())
            throw new ValidationException(GET_KIT_CUSTOM_DATA_KIT_CUSTOM_ID_INVALID);

        var subjectIdToWeight = kitCustom.customData().subjects().stream()
            .collect(Collectors.toMap(KitCustomData.Subject::id, KitCustomData.Subject::weight));
        var attributeIdToWeight = kitCustom.customData().attributes().stream()
            .collect(Collectors.toMap(KitCustomData.Attribute::id, KitCustomData.Attribute::weight));

        var paginatedResponse = loadSubjectPort.loadWithAttributesByKitVersionId(kit.getActiveVersionId(),
            param.getPage(),
            param.getSize());

        var items = paginatedResponse.getItems().stream()
            .map(e -> {
                var attributes = e.getAttributes().stream()
                    .map(x -> {
                        boolean customized = attributeIdToWeight.containsKey(x.getId());
                        int weight = x.getWeight();
                        if (customized)
                            weight = attributeIdToWeight.get(x.getId());
                        return new Result.Subject.Attribute(x.getId(), x.getTitle(), weight, customized);})
                    .toList();

                boolean customized = subjectIdToWeight.containsKey(e.getId());
                Integer weight = e.getWeight();
                if (customized)
                    weight = subjectIdToWeight.get(e.getId());
                var subject = new Result.Subject(e.getId(), e.getTitle(), weight, attributes, customized);

                return new Result(subject);})
            .toList();

        return new PaginatedResponse<>(items,
            paginatedResponse.getPage(),
            paginatedResponse.getSize(),
            paginatedResponse.getSort(),
            paginatedResponse.getOrder(),
            paginatedResponse.getTotal());
    }
}
