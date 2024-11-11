package org.flickit.assessment.kit.application.port.out.answerange;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.AnswerRange;

public interface LoadAnswerRangesPort {

    PaginatedResponse<AnswerRange> loadByKitVersionId(long kitVersionId, int page, int size);
}
