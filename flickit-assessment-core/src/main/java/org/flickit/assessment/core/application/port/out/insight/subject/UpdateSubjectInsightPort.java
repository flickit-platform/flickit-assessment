package org.flickit.assessment.core.application.port.out.insight.subject;

import org.flickit.assessment.core.application.domain.insight.SubjectInsight;

import java.util.List;

public interface UpdateSubjectInsightPort {

    void update(SubjectInsight subjectInsight);

    void updateAll(List<SubjectInsight> subjectInsights);
}
