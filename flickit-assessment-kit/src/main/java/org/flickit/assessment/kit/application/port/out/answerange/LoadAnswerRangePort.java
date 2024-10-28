package org.flickit.assessment.kit.application.port.out.answerange;

import org.flickit.assessment.kit.application.domain.AnswerRange;

import java.util.List;

public interface LoadAnswerRangePort {

    List<AnswerRange> loadByKitVersionId(long kitVersionId);
}
