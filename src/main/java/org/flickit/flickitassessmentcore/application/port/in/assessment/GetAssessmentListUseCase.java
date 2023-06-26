package org.flickit.flickitassessmentcore.application.port.in.assessment;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;
import org.flickit.flickitassessmentcore.domain.Assessment;

import java.util.List;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.GET_ASSESSMENT_LIST_SPACE_ID_NOT_NULL;

public interface GetAssessmentListUseCase {

    Result viewListOfSpaceAssessments(Param param);

    @Value
    class Param extends SelfValidating<Param> {
        @NotNull(message = GET_ASSESSMENT_LIST_SPACE_ID_NOT_NULL)
        Long spaceId;

        public Param(Long spaceId) {
            this.spaceId = spaceId;
            this.validateSelf();
        }
    }

    record Result(List<Assessment> assessments){}

}
