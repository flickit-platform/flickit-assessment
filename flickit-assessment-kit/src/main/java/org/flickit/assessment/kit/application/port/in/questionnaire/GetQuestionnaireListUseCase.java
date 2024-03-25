package org.flickit.assessment.kit.application.port.in.questionnaire;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.kit.application.domain.Questionnaire;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_QUESTIONNAIRE_LIST_ASSESSMENT_ID_NOT_NULL;

public interface GetQuestionnaireListUseCase {

    List<Questionnaire> getQuestionnaireList(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_QUESTIONNAIRE_LIST_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        public Param(UUID assessmentId) {
            this.assessmentId = assessmentId;
            this.validateSelf();
        }
    }

    record QuestionnaireListItem(
        long id,
        String title,
        int index,
        int questionCount,
        int answersCount,
        int progress,
        List<Subject> subjects
    ) {
    }

    record Subject(
        long id,
        String title
    ) {
    }
}
