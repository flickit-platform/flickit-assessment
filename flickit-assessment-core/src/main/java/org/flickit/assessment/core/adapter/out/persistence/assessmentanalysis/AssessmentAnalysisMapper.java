package org.flickit.assessment.core.adapter.out.persistence.assessmentanalysis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.AnalysisType;
import org.flickit.assessment.core.application.domain.AssessmentAnalysis;
import org.flickit.assessment.core.application.port.out.assessmentanalysis.CreateAssessmentAnalysisPort;
import org.flickit.assessment.data.jpa.core.assessmentanalysis.AssessmentAnalysisJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentAnalysisMapper {

    public static AssessmentAnalysisJpaEntity toJpaEntity(CreateAssessmentAnalysisPort.Param assessmentAnalysis) {
        return new AssessmentAnalysisJpaEntity(
            null,
            assessmentAnalysis.assessmentResultId(),
            assessmentAnalysis.type().getId(),
            null,
            null,
            null,
            null,
            assessmentAnalysis.inputPath()
        );
    }

    public static AssessmentAnalysis mapToDomain(AssessmentAnalysisJpaEntity entity) {
        return new AssessmentAnalysis(
            entity.getId(),
            entity.getAssessmentResultId(),
            AnalysisType.valueOfById(entity.getType()),
            entity.getAiAnalysis(),
            entity.getAssessorAnalysis(),
            entity.getAiAnalysisTime(),
            entity.getAssessorAnalysisTime(),
            entity.getInputPath()
        );
    }
}
