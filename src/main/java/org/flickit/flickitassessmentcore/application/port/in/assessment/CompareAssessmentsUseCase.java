package org.flickit.flickitassessmentcore.application.port.in.assessment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.flickitassessmentcore.application.domain.crud.AssessmentListItem;
import org.flickit.flickitassessmentcore.application.domain.report.TopAttribute;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;

public interface CompareAssessmentsUseCase {

    List<CompareListItem> compareAssessments(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = COMPARE_ASSESSMENTS_ASSESSMENT_IDS_NOT_NULL)
        @Size(min = 2, message = COMPARE_ASSESSMENTS_ASSESSMENT_IDS_SIZE_MIN)
        @Size(max = 4, message = COMPARE_ASSESSMENTS_ASSESSMENT_IDS_SIZE_MAX)
        LinkedHashSet<UUID> assessmentIds;

        public Param(List<UUID> assessmentIds) {
            this.assessmentIds = assessmentIds == null ? null : new LinkedHashSet<>(assessmentIds);
            this.validateSelf();
        }
    }

    record CompareListItem(
        AssessmentListItem assessment,
        Integer answeredQuestions,
        List<TopAttribute> topStrengths,
        List<TopAttribute> topWeaknesses
    ) {
    }
}
