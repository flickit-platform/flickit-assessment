package org.flickit.assessment.kit.application.service.assessmentkit.validate.impl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;
import org.flickit.assessment.kit.application.service.assessmentkit.validate.UpdateKitValidator;
import org.flickit.assessment.common.exception.api.Notification;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.toSet;
import static org.flickit.assessment.kit.application.service.assessmentkit.validate.impl.DslFieldNames.QUESTIONNAIRE;

@Service
@RequiredArgsConstructor
public class QuestionnaireUpdateKitValidator implements UpdateKitValidator {

    @Override
    public Notification validate(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        Notification notification = new Notification();

        var savedQuestionnaireCodes = savedKit.getQuestionnaires().stream().map(Questionnaire::getCode).collect(toSet());
        var dslQuestionnaireCodes = dslKit.getQuestionnaires().stream().map(QuestionnaireDslModel::getCode).collect(toSet());

        var deletedQuestionnaires = savedQuestionnaireCodes.stream()
            .filter(s -> !dslQuestionnaireCodes.contains(s))
            .collect(toSet());

        if (!deletedQuestionnaires.isEmpty())
            notification.add(new InvalidDeletionError(QUESTIONNAIRE, deletedQuestionnaires));

        return notification;
    }
}
