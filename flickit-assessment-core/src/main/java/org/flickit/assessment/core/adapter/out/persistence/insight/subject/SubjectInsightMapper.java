package org.flickit.assessment.core.adapter.out.persistence.insight.subject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.insight.SubjectInsight;
import org.flickit.assessment.data.jpa.core.insight.subject.SubjectInsightJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SubjectInsightMapper {

    public static SubjectInsightJpaEntity mapToJpaEntity(SubjectInsight subjectInsight) {
        return new SubjectInsightJpaEntity(subjectInsight.getAssessmentResultId(),
            subjectInsight.getSubjectId(),
            subjectInsight.getInsight(),
            subjectInsight.getInsightTime(),
            subjectInsight.getLastModificationTime(),
            subjectInsight.getInsightBy(),
            subjectInsight.isApproved());
    }

    public static SubjectInsight mapToDomainModel(SubjectInsightJpaEntity entity) {
        return new SubjectInsight(entity.getAssessmentResultId(),
            entity.getSubjectId(),
            entity.getInsight(),
            entity.getInsightTime(),
            entity.getLastModificationTime(),
            entity.getInsightBy(),
            entity.getApproved());
    }
}
