package org.flickit.assessment.core.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.evidence.GetAttributeEvidenceListUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CheckAssessmentExistencePort;
import org.flickit.assessment.core.application.port.out.evidence.LoadAttributeEvidencesPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ATTRIBUTE_EVIDENCE_LIST_ASSESSMENT_ID_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAttributeEvidenceListService implements GetAttributeEvidenceListUseCase {

    private final LoadAttributeEvidencesPort loadAttributeEvidencesPort;
    private final CheckAssessmentExistencePort checkAssessmentExistencePort;

    @Override
    public PaginatedResponse<AttributeEvidenceListItem> getAttributeEvidenceList(Param param) {
        if (!checkAssessmentExistencePort.existsById(param.getAssessmentId()))
            throw new ResourceNotFoundException(GET_ATTRIBUTE_EVIDENCE_LIST_ASSESSMENT_ID_NOT_FOUND);
        return loadAttributeEvidencesPort.loadAttributeEvidences(param.getAssessmentId(),
            param.getAttributeId(),
            param.getPage(),
            param.getSize());
    }
}
