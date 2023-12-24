package org.flickit.assessment.core.application.port.out.question;

import org.flickit.assessment.core.application.port.in.assessment.GetAttributeScoreDetailUseCase;

import java.util.List;
import java.util.UUID;

public interface LoadAttributeScoreDetailPort {

    List<GetAttributeScoreDetailUseCase.QuestionScore> load(long attributeId, long maturityLevelId, UUID assessmentResultId);
}
