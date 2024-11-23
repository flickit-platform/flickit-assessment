package org.flickit.assessment.kit.application.service.kitcustom;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.KitCustomData;
import org.flickit.assessment.kit.application.port.in.kitcustom.GetKitCustomSubjectUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.kitcustom.LoadKitCustomPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.CheckKitUserAccessPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitCustomSubjectService implements GetKitCustomSubjectUseCase {

    private final LoadSubjectsPort loadSubjectsPort;
    private final LoadAssessmentKitPort loadAssessmentKitPort;
    private final CheckKitUserAccessPort checkKitUserAccessPort;
    private final LoadKitCustomPort loadKitCustomPort;

    @Override
    public PaginatedResponse<Result> getKitCustomSubject(Param param) {
        AssessmentKit kit = loadAssessmentKitPort.load(param.getKitId());
        if (kit.isPrivate() && !checkKitUserAccessPort.hasAccess(param.getKitId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        Map<Long, Integer> subjectIdToWeight;
        Map<Long, Integer> attributeIdToWeight;
        if (param.getKitCustomId() != null) {
            var kitCustom = loadKitCustomPort.loadById(param.getKitCustomId(), param.getKitId());

            subjectIdToWeight = kitCustom.customData().subjects() != null ? kitCustom.customData().subjects().stream()
                .collect(Collectors.toMap(KitCustomData.Subject::id, KitCustomData.Subject::weight)) : new HashMap<>();

            attributeIdToWeight = kitCustom.customData().attributes() != null ? kitCustom.customData().attributes().stream()
                .collect(Collectors.toMap(KitCustomData.Attribute::id, KitCustomData.Attribute::weight)) : new HashMap<>();
        } else {
            subjectIdToWeight = new HashMap<>();
            attributeIdToWeight = new HashMap<>();
        }

        var paginatedResponse = loadSubjectsPort.loadWithAttributesByKitVersionId(kit.getActiveVersionId(),
            param.getPage(),
            param.getSize());

        var items = paginatedResponse.getItems().stream()
            .map(e -> {
                var attributes = e.getAttributes().stream()
                    .map(x -> new Result.Subject.Attribute(x.getId(),
                            x.getTitle(),
                            new Result.Weight(x.getWeight(), attributeIdToWeight.get(x.getId()))))
                    .toList();
                var subject = new Result.Subject(e.getId(),
                        e.getTitle(),
                        new Result.Weight(e.getWeight(), subjectIdToWeight.get(e.getId())), attributes);

                return new Result(subject);
            }).toList();

        return new PaginatedResponse<>(items,
            paginatedResponse.getPage(),
            paginatedResponse.getSize(),
            paginatedResponse.getSort(),
            paginatedResponse.getOrder(),
            paginatedResponse.getTotal());
    }
}
