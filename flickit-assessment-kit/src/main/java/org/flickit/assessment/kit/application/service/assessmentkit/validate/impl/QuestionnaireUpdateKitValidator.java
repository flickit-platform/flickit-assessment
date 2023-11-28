package org.flickit.assessment.kit.application.service.assessmentkit.validate.impl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;
import org.flickit.assessment.kit.application.service.assessmentkit.validate.UpdateKitValidator;
import org.flickit.assessment.kit.common.Notification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_KIT_BY_DSL_QUESTIONNAIRE_DELETION_UNSUPPORTED;

@Service
@RequiredArgsConstructor
public class QuestionnaireUpdateKitValidator implements UpdateKitValidator {

    @Override
    public Notification validate(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        Notification result = new Notification();

        List<Questionnaire> savedQuestionnaires = savedKit.getQuestionnaires();
        List<QuestionnaireDslModel> dslQuestionnaires = dslKit.getQuestionnaires();

        List<String> savedQuestionnaireCodes = savedQuestionnaires.stream().map(Questionnaire::getCode).toList();
        List<String> dslQuestionnaireCodes = dslQuestionnaires.stream().map(QuestionnaireDslModel::getCode).toList();

        List<String> deletedQuestionnaires = savedQuestionnaireCodes.stream()
            .filter(s -> dslQuestionnaireCodes.stream()
                .noneMatch(i -> i.equals(s)))
            .toList();

        if (!deletedQuestionnaires.isEmpty()) {
            String deletedCodes = String.join(", ", deletedQuestionnaires);
            result.add(new Notification.Error(UPDATE_KIT_BY_DSL_QUESTIONNAIRE_DELETION_UNSUPPORTED, deletedCodes));
        }

        return result;
    }
}
