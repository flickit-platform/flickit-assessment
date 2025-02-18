package org.flickit.assessment.core.application.port.out.subjectinsight;

import org.flickit.assessment.core.application.domain.SubjectInsight;

import java.util.Collection;

public interface CreateSubjectInsightPort {

    void persist(SubjectInsight subjectInsight);

    void persistAll(Collection<SubjectInsight> subjectInsights);
}
