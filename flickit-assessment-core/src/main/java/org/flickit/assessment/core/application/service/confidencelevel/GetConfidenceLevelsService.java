package org.flickit.assessment.core.application.service.confidencelevel;

import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.port.in.confidencelevel.GetConfidenceLevelsUseCase;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class GetConfidenceLevelsService implements GetConfidenceLevelsUseCase {

    @Override
    public Result getConfidenceLevels() {
        var defaultConfidenceLevel = ConfidenceLevel.getDefault();
        var defaultConfidenceLevelItem = new ConfidenceLevelItem(defaultConfidenceLevel.getId(), defaultConfidenceLevel.getTitle());

        var confidenceLevelItems = Arrays.stream(ConfidenceLevel.values())
            .map(cl -> new ConfidenceLevelItem(cl.getId(), cl.getTitle()))
            .toList();

        return new Result(defaultConfidenceLevelItem, confidenceLevelItems);
    }
}
