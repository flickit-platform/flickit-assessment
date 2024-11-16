package org.flickit.assessment.kit.application.service.kitversion.validatekitversion.impl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.api.Notification;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.service.kitversion.validatekitversion.KitVersionValidator;
import org.springframework.stereotype.Service;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@Service
@RequiredArgsConstructor
public class QuestionImpactValidator implements KitVersionValidator {

    @Override
    public Notification validate(KitVersion kitVersion) {
        var questionnaires = kitVersion.getKit().getQuestionnaires();

        var emptyQuestionImpact = questionnaires
            .stream()
            .map(Questionnaire::getQuestions)
            .map(Question::getImpacts)

        return new Notification().add(VALIDATE_KIT_VERSION_EMPTY_QUESTION_IMPACT_UNSUPPORTED);
    }
}
