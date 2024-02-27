package org.flickit.assessment.core.application.service.evidence;

import org.flickit.assessment.core.application.domain.EvidenceType;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceTypesUseCase;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GetEvidenceTypesServiceTest {

    private final GetEvidenceTypesService service = new GetEvidenceTypesService();

    @Test
    void testGetEvidenceTypes() {
        List<GetEvidenceTypesUseCase.EvidenceTypeItem> items = Arrays.stream(EvidenceType.values())
            .map(e -> new GetEvidenceTypesUseCase.EvidenceTypeItem(e.getCode(), e.getTitle()))
            .toList();

        assertEquals(new GetEvidenceTypesUseCase.Result(items), service.getEvidenceTypes());
    }
}
