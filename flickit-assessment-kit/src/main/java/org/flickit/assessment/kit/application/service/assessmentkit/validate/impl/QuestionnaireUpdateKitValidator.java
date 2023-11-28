package org.flickit.assessment.kit.application.service.assessmentkit.validate.impl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;
import org.flickit.assessment.kit.application.service.assessmentkit.validate.UpdateKitValidator;
import org.flickit.assessment.kit.common.MessageBundle;
import org.flickit.assessment.kit.common.Notification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_KIT_BY_DSL_QUESTIONNAIRE_DELETION_UNSUPPORTED;

@Service
@RequiredArgsConstructor
public class QuestionnaireUpdateKitValidator implements UpdateKitValidator {

    @Override
    public Notification validate(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        Notification result = new Notification();

        List<Questionnaire> savedQuestionnaires = savedKit.getQuestionnaires();
        List<QuestionnaireDslModel> dslQuestionnaires = dslKit.getQuestionnaires();

        Set<String> savedQuestionnaireCodes = savedQuestionnaires.stream().map(Questionnaire::getCode).collect(toSet());
        Set<String> dslQuestionnaireCodes = dslQuestionnaires.stream().map(QuestionnaireDslModel::getCode).collect(toSet());

        Set<String> deletedQuestionnaires = savedQuestionnaireCodes.stream()
            .filter(s -> !dslQuestionnaireCodes.contains(s))
            .collect(toSet());

        if (!deletedQuestionnaires.isEmpty())
            result.add(new InvalidQuestionnaireUpdateError(deletedQuestionnaires));

        return result;
    }

    public record InvalidQuestionnaireUpdateError(Set<String> deletedCodes) implements Notification.Error {
        @Override
        public String message() {
            String deletedCodesStr = String.join(", ", deletedCodes);
            return MessageBundle.message(UPDATE_KIT_BY_DSL_QUESTIONNAIRE_DELETION_UNSUPPORTED, deletedCodesStr);
        }
    }
}
