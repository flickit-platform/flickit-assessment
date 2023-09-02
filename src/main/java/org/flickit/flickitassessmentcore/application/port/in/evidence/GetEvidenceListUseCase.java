package org.flickit.flickitassessmentcore.application.port.in.evidence;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.flickit.flickitassessmentcore.application.domain.Evidence;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.util.List;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;

public interface GetEvidenceListUseCase {

    Result getEvidenceList(Param param);

    @Value
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_EVIDENCE_LIST_QUESTION_ID_NOT_NULL)
        Long questionId;

        @Min(value = 10, message = GET_EVIDENCE_LIST_SIZE_MIN)
        @Max(value = 100, message = GET_EVIDENCE_LIST_SIZE_MAX)
        int size;

        @Min(value = 0, message = GET_EVIDENCE_LIST_PAGE_MIN)
        int page;

        public Param(Long questionId, int size, int page) {
            this.questionId = questionId;
            this.size = size;
            this.page = page;
            this.validateSelf();
        }
    }

    record Result(List<Evidence> evidences){}
}
