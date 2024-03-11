package org.flickit.assessment.core.application.service.evidence;

import org.flickit.assessment.core.application.domain.EvidenceType;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceTypesUseCase;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GetEvidenceTypesService implements GetEvidenceTypesUseCase {

    @Override
    public Result getEvidenceTypes() {
        List<EvidenceTypeItem> items = Arrays.stream(EvidenceType.values())
            .map(e -> new EvidenceTypeItem(e.getCode(), e.getTitle()))
            .toList();
        return new Result(items);
    }
}
