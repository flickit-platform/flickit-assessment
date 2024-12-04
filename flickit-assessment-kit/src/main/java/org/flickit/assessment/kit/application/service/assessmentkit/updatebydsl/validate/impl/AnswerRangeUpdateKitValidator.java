package org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.validate.impl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.api.Notification;
import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.domain.AnswerRange;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AnswerRangeDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.validate.UpdateKitValidator;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class AnswerRangeUpdateKitValidator implements UpdateKitValidator {

    @Override
    public Notification validate(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        Notification notification = new Notification();

        var codeToAnswerRange = savedKit.getReusableAnswerRanges().stream()
            .collect(toMap(AnswerRange::getCode, Function.identity()));

        var codeToDslAnswerRange = dslKit.getAnswerRanges().stream()
            .collect(toMap(AnswerRangeDslModel::getCode, Function.identity()));

        var deletedCodes = codeToAnswerRange.keySet().stream()
            .filter(s -> !codeToDslAnswerRange.containsKey(s))
            .collect(toSet());

        if (!deletedCodes.isEmpty())
            notification.add(new InvalidDeletionError(DslFieldNames.ANSWER_RANGE, deletedCodes));

        validateOptions(codeToAnswerRange, codeToDslAnswerRange, notification);

        return notification;
    }

    private void validateOptions(Map<String, AnswerRange> codeToAnswerRange, Map<String, AnswerRangeDslModel> codeToDslRange, Notification notification) {
        for (Map.Entry<String, AnswerRange> rangeEntry : codeToAnswerRange.entrySet()) {
            Map<Integer, AnswerOption> savedOptionIndexMap = rangeEntry.getValue()
                .getAnswerOptions().stream()
                .collect(toMap(AnswerOption::getIndex, a -> a));

            AnswerRangeDslModel dslRange = codeToDslRange.get(rangeEntry.getKey());
            if (dslRange == null)
                continue;

            Map<Integer, AnswerOptionDslModel> dslOptionIndexMap = dslRange.getAnswerOptions().stream()
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
}
