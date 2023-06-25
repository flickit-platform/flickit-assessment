package org.flickit.flickitassessmentcore.application.port.in.evidence;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;
import org.flickit.flickitassessmentcore.domain.Evidence;

import java.util.List;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.GET_EVIDENCE_LIST_QUESTION_ID_NOT_NULL;

public interface GetEvidenceListUseCase {

    Result getEvidenceList(Param param);

    @Value
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_EVIDENCE_LIST_QUESTION_ID_NOT_NULL)
        Long questionId;

        public Param(Long questionId) {
            this.questionId = questionId;
            this.validateSelf();
        }
    }

    record Result(List<Evidence> evidences){}
}
