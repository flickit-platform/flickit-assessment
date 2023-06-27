package org.flickit.flickitassessmentcore.application.port.in.assessment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;
import org.flickit.flickitassessmentcore.domain.Assessment;

import java.util.List;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;

public interface GetAssessmentListUseCase {

    Result getAssessmentList(Param param);

    @Value
    class Param extends SelfValidating<Param> {
        @NotNull(message = GET_ASSESSMENT_LIST_SPACE_ID_NOT_NULL)
        Long spaceId;

        @Min(value = 1, message = GET_ASSESSMENT_LIST_SIZE_MIN)
        @Max(value = 100, message = GET_ASSESSMENT_LIST_SIZE_MAX)
        int size;

        @Min(value = 0, message = GET_ASSESSMENT_LIST_PAGE_MIN)
        int page;

        public Param(Long spaceId, int size, int page) {
            this.spaceId = spaceId;
            this.size = size;
            this.page = page;
            this.validateSelf();
        }
    }

    record Result(List<Assessment> assessments) {
    }

}
