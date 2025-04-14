package org.flickit.assessment.kit.application.service.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AttributeWithSubject;
import org.flickit.assessment.kit.application.port.in.attribute.GetAttributesUseCase;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAttributesService implements GetAttributesUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadAttributesPort loadAttributesPort;

    @Override
    public PaginatedResponse<AttributeListItem> getAttributes(Param param) {
        var kit = loadKitVersionPort.load(param.getKitVersionId()).getKit();
        if (!checkExpertGroupAccessPort.checkIsMember(kit.getExpertGroupId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var paginatedResponse = loadAttributesPort.loadByKitVersionId(param.getKitVersionId(), param.getSize(), param.getPage());

        var items = paginatedResponse.getItems().stream()
            .map(this::mapToAttributeListItem)
            .toList();
        return new PaginatedResponse<>(
            items,
            paginatedResponse.getPage(),
            paginatedResponse.getSize(),
            paginatedResponse.getSort(),
            paginatedResponse.getOrder(),
            paginatedResponse.getTotal());
    }

    private AttributeListItem mapToAttributeListItem(AttributeWithSubject attributeWithSubject) {
        var subject = attributeWithSubject.subject();
        var attribute = attributeWithSubject.attribute();
        return new AttributeListItem(attribute.getId(),
            attribute.getIndex(),
            attribute.getTitle(),
            attribute.getDescription(),
            attribute.getWeight(),
            new AttributeSubject(subject.getId(), subject.getTitle()));
    }
}
