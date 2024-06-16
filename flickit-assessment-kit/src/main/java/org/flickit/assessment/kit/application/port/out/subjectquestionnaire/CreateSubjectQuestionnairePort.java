package org.flickit.assessment.kit.application.port.out.subjectquestionnaire;

import java.util.Map;
import java.util.Set;

public interface CreateSubjectQuestionnairePort {

    long persist(long subjectId, long questionnaireId, Long kitVersionId);

    void persistAll(Map<Long, Set<Long>> questionnaireIdToSubjectIdsMap, Long kitVersionId);
}
