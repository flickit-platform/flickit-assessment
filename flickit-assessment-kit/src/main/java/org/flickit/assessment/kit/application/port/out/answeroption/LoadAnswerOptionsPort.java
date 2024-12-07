package org.flickit.assessment.kit.application.port.out.answeroption;

import org.flickit.assessment.kit.application.domain.AnswerOption;

import java.util.List;
import java.util.Set;

public interface LoadAnswerOptionsPort {

    List<AnswerOption> loadByQuestionId(Long questionId, Long kitVersionId);

    List<AnswerOption> loadByRangeIdInAndKitVersionId(Set<Long> rangeIds, long kitVersionId);

    List<AnswerOption> loadByRangeIdAndKitVersionId(long rangeId, long kitVersionId);
}
