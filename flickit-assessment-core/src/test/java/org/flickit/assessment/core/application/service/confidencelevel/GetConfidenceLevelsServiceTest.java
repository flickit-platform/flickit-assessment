package org.flickit.assessment.core.application.service.confidencelevel;

import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.port.in.confidencelevel.GetConfidenceLevelsUseCase.ConfidenceLevelItem;
import org.flickit.assessment.core.application.port.in.confidencelevel.GetConfidenceLevelsUseCase.Result;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetConfidenceLevelsServiceTest {

    private final GetConfidenceLevelsService service = new GetConfidenceLevelsService();

    @Test
    void testGetConfidenceLevels() {
        var defaultConfidenceLevel = ConfidenceLevel.getDefault();
        var defaultConfidenceLevelItem = new ConfidenceLevelItem(defaultConfidenceLevel.getId(), defaultConfidenceLevel.getTitle());
        List<ConfidenceLevelItem> confidenceLevelItems = Arrays.stream(ConfidenceLevel.values())
            .map(cl -> new ConfidenceLevelItem(cl.getId(), cl.getTitle()))
            .toList();

        assertEquals(new Result(defaultConfidenceLevelItem, confidenceLevelItems), service.getConfidenceLevels());
    }
}
