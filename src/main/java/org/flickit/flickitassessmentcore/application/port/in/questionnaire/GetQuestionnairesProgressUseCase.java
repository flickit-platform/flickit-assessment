package org.flickit.flickitassessmentcore.application.port.in.questionnaire;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.util.List;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.GET_QUESTIONNAIRES_PROGRESS_ASSESSMENT_ID_NOT_NULL;

public interface GetQuestionnairesProgressUseCase {

    Result getQuestionnairesProgress(Param param);

    @Value
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_QUESTIONNAIRES_PROGRESS_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        public Param(UUID assessmentId) {
            this.assessmentId = assessmentId;
            this.validateSelf();
        }
    }

    record Result(Progress<UUID> assessmentProgress, List<Progress<Long>> questionnairesProgress){
    }

    record Progress<ID>(ID id, Integer answersCount) {
    }
}
