package org.flickit.assessment.core.adapter.out.persistence.subjectinsight;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.SubjectInsight;
import org.flickit.assessment.data.jpa.core.subjectinsight.SubjectInsightJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SubjectInsightMapper {

    public static SubjectInsightJpaEntity mapToJpaEntity(SubjectInsight subjectInsight) {
        return new SubjectInsightJpaEntity(subjectInsight.getAssessmentResultId(),
                subjectInsight.getSubjectId(),
                subjectInsight.getInsight(),
                subjectInsight.getInsightTime(),
                subjectInsight.getInsightBy());
    }

    public static SubjectInsight mapToDomainModel(SubjectInsightJpaEntity entity, boolean isValid) {
        return new SubjectInsight(entity.getAssessmentResultId(),
                entity.getSubjectId(),
                entity.getInsight(),
                entity.getInsightTime(),
                entity.getInsightBy(),
                isValid);
    }
}
