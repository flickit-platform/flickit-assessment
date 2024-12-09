package org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.validate.impl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.api.Notification;
import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionDslModel;
import org.flickit.assessment.kit.application.port.out.answerrange.LoadAnswerRangePort;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.validate.UpdateKitValidator;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
public class QuestionUpdateKitValidator implements UpdateKitValidator {

    private final LoadAnswerRangePort loadAnswerRangePort;

    @Override
    public Notification validate(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        Notification notification = new Notification();

        Map<String, Map<String, Question>> savedQuestionnaireToQuestionsMap = savedKit.getQuestionnaires().stream()
            .collect(toMap(Questionnaire::getCode, q -> {
                if (q.getQuestions() == null)
                    return Map.of();
                return q.getQuestions().stream()
                    .collect(toMap(Question::getCode, s -> s));
            }));
        Map<String, Map<String, QuestionDslModel>> dslQuestionnaireToQuestionsMap = dslKit.getQuestions().stream()
            .collect(groupingBy(QuestionDslModel::getQuestionnaireCode,
                toMap(QuestionDslModel::getCode, model -> model)
            ));

        for (Map.Entry<String, Map<String, Question>> questionnaireEntry : savedQuestionnaireToQuestionsMap.entrySet()) {
            Map<String, Question> codeToQuestion = questionnaireEntry.getValue();
            Map<String, QuestionDslModel> codeToDslQuestion = dslQuestionnaireToQuestionsMap.get(questionnaireEntry.getKey());

            if (codeToDslQuestion == null) // handled in QuestionnaireUpdateKitValidator
                continue;

            var deletedQuestions = codeToQuestion.keySet().stream()
                .filter(s -> !codeToDslQuestion.containsKey(s))
                .collect(toSet());

            if (!deletedQuestions.isEmpty())
                notification.add(new InvalidDeletionError(DslFieldNames.QUESTION, deletedQuestions));

            validateQuestionnaireQuestions(notification, codeToQuestion, codeToDslQuestion, savedKit.getActiveVersionId());
        }

        return notification;
    }

    private void validateQuestionnaireQuestions(Notification notification,
                                                Map<String, Question> codeToQuestion,
                                                Map<String, QuestionDslModel> codeToDslQuestion,
                                                long kitVersionId) {
        Set<String> invalidChanges = new HashSet<>();

        for (Map.Entry<String, Question> questionEntry : codeToQuestion.entrySet()) {
            QuestionDslModel dslQuestion = codeToDslQuestion.get(questionEntry.getKey());
            if (dslQuestion == null) // handled before
                continue;

            var savedQuestion = questionEntry.getValue();
            var range = loadAnswerRangePort.load(savedQuestion.getAnswerRangeId(), kitVersionId);
            if (range.isReusable() && range.getCode().equals(dslQuestion.getAnswerRangeCode()))
                continue;
            if (!range.isReusable() && dslQuestion.getAnswerOptions() != null) {
                validateOptions(notification, savedQuestion, dslQuestion);
                continue;
            }
            invalidChanges.add(savedQuestion.getCode());
        }

        if (!invalidChanges.isEmpty())
            notification.add(new InvalidChangeError(DslFieldNames.QUESTION, invalidChanges));
    }

    private static void validateOptions(Notification notification, Question savedQuestion, QuestionDslModel dslQuestion) {
        Map<Integer, AnswerOption> savedOptionIndexMap = savedQuestion.getOptions().stream()
            .collect(toMap(AnswerOption::getIndex, a -> a));
        Map<Integer, AnswerOptionDslModel> dslOptionIndexMap = dslQuestion.getAnswerOptions().stream()
            .collect(toMap(AnswerOptionDslModel::getIndex, a -> a));

        var deletedOptions = savedOptionIndexMap.entrySet().stream()
            .filter(savedOption -> !dslOptionIndexMap.containsKey(savedOption.getKey()))
            .map(answerOption -> answerOption.getValue().getTitle())
            .collect(toSet());

        var newOptions = dslOptionIndexMap.entrySet().stream()
            .filter(dslOption -> !savedOptionIndexMap.containsKey(dslOption.getKey()))
            .map(answerOption -> answerOption.getValue().getCaption())
            .collect(toSet());

        if (!deletedOptions.isEmpty())
            notification.add(new InvalidDeletionError(DslFieldNames.ANSWER_OPTION, deletedOptions));

        if (!newOptions.isEmpty())
            notification.add(new InvalidAdditionError(DslFieldNames.ANSWER_OPTION, newOptions));
    }
}
