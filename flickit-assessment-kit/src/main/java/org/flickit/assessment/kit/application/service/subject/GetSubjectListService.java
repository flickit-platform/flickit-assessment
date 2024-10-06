package org.flickit.assessment.kit.application.service.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.subject.GetSubjectListUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetSubjectListService implements GetSubjectListUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadSubjectsPort loadSubjectsPort;

    @Override
    public PaginatedResponse<SubjectListItem> getSubjectList(Param param) {
        var kitVersion = loadKitVersionPort.load(param.getKitVersionId());

        if (!checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var portResult = loadSubjectsPort.loadPaginatedByKitVersionId(kitVersion.getId(), param.getPage(), param.getSize());

        return new PaginatedResponse<>(
            portResult.getItems().stream().map(this::mapToSubjectListItem).toList(),
            param.getPage(),
            param.getSize(),
            portResult.getSort(),
            portResult.getOrder(),
            portResult.getTotal());
    }

    private SubjectListItem mapToSubjectListItem(LoadSubjectsPort.Result portResult) {
        return new SubjectListItem(portResult.id(),
            portResult.title(),
            portResult.description(),
            portResult.index(),
            portResult.weight());
    }
}
