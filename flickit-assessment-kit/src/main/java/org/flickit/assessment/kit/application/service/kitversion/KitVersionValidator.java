package org.flickit.assessment.kit.application.service.kitversion;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.kit.application.port.out.answerrange.LoadAnswerRangesPort;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionsPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectsPort;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@Service
@Transactional
@RequiredArgsConstructor
public class KitVersionValidator {

    private final LoadQuestionsPort loadQuestionsPort;
    private final LoadAnswerRangesPort loadAnswerRangesPort;
    private final LoadSubjectsPort loadSubjectsPort;
    private final LoadAttributesPort loadAttributesPort;

    public List<String> validate(long kitVersionId) {
        List<String> errors = new LinkedList<>();
        errors.addAll(loadQuestionsPort.loadQuestionsWithoutImpact(kitVersionId)
            .stream()
            .map(e -> MessageBundle.message(VALIDATE_KIT_VERSION_QUESTION_IMPACT_NOT_NULL, e.questionIndex(), e.questionnaireTitle()))
            .toList());

        errors.addAll(loadQuestionsPort.loadQuestionsWithoutAnswerRange(kitVersionId)
            .stream()
            .map(e -> MessageBundle.message(VALIDATE_KIT_VERSION_QUESTION_ANSWER_RANGE_NOT_NULL, e.questionIndex(), e.questionnaireTitle()))
            .toList());

        errors.addAll(loadAnswerRangesPort.loadAnswerRangesWithNotEnoughOptions(kitVersionId)
            .stream()
            .map(e -> MessageBundle.message(VALIDATE_KIT_VERSION_ANSWER_RANGE_LOW_OPTIONS, e.getTitle()))
            .toList());

        errors.addAll(loadAttributesPort.loadUnimpactedAttributes(kitVersionId)
            .stream()
            .map(e -> MessageBundle.message(VALIDATE_KIT_VERSION_ATTRIBUTE_QUESTION_IMPACT_NOT_NULL, e.getTitle()))
            .toList());

        errors.addAll(loadSubjectsPort.loadSubjectsWithoutAttribute(kitVersionId)
            .stream()
            .map(e -> MessageBundle.message(VALIDATE_KIT_VERSION_SUBJECT_ATTRIBUTE_NOT_NULL, e.getTitle()))
            .toList());

        return errors;
    }
}
