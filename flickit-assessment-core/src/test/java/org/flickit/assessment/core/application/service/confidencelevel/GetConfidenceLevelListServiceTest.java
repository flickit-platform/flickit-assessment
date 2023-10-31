package org.flickit.assessment.core.application.service.confidencelevel;

import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.port.in.confidencelevel.GetConfidenceLevelListUseCase.ConfidenceLevelItem;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetConfidenceLevelListServiceTest {

    private final GetConfidenceLevelListService service = new GetConfidenceLevelListService();

    @Test
    void testGetConfidenceLevelList() {
        List<ConfidenceLevelItem> confidenceLevelItems = Arrays.stream(ConfidenceLevel.values())
            .map(cl -> new ConfidenceLevelItem(cl.getId(), cl.getTitle()))
            .toList();

        assertEquals(confidenceLevelItems, service.getConfidenceLevels());
    }
}
