package org.flickit.flickitassessmentcore.application.port.in.assessment;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;
import org.flickit.flickitassessmentcore.domain.Assessment;

import java.util.List;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.CREATE_ASSESSMENT_SPACE_ID_NOT_NULL;

public interface ViewListOfSpaceAssessmentsUseCase {

    public Result viewListOfSpaceAssessments(Param param);

    @Value
    class Param extends SelfValidating<Param> {
        @NotNull(message = CREATE_ASSESSMENT_SPACE_ID_NOT_NULL)
        Long spaceId;

        public Param(Long spaceId) {
            this.spaceId = spaceId;
            this.validateSelf();
        }
    }

    record Result(List<Assessment> assessments){}

}
