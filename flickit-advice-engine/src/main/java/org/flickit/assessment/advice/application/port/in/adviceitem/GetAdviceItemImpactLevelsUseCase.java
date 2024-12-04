package org.flickit.assessment.advice.application.port.in.adviceitem;

import java.util.List;

public interface GetAdviceItemImpactLevelsUseCase {

    Result getImpactLevels();

    record Result(List<AdviceItemImpactLevel> levels) { }

    record AdviceItemImpactLevel(String code, String title) { }
}
