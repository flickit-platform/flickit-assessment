package org.flickit.assessment.core.application.service.confidencelevel;

import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.port.in.confidencelevel.GetConfidenceLevelListUseCase;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GetConfidenceLevelListService implements GetConfidenceLevelListUseCase {

    @Override
    public List<ConfidenceLevelItem> getConfidenceLevels() {
        return Arrays.stream(ConfidenceLevel.values())
            .map(cl -> new ConfidenceLevelItem(cl.getId(), cl.getTitle()))
            .toList();
    }
}
