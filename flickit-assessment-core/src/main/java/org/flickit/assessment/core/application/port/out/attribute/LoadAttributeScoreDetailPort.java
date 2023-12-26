package org.flickit.assessment.core.application.port.out.attribute;

import org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreDetailUseCase;

import java.util.List;
import java.util.UUID;

public interface LoadAttributeScoreDetailPort {

    List<GetAttributeScoreDetailUseCase.QuestionScore> loadScoreDetail(UUID assessmentResultId, long attributeId, long maturityLevelId);
}
