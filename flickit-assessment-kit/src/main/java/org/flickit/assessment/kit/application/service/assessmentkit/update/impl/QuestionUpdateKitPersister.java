package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.domain.QuestionImpact;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionImpactDslModel;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.UpdateQuestionImpactPort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionUpdateKitPersister implements UpdateKitPersister {

    private final UpdateQuestionPort updateQuestionPort;
    private final UpdateQuestionImpactPort updateQuestionImpactPort;

    @Override
    public UpdateKitPersisterResult persist(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        var savedQuestions = savedKit.getQuestionnaires().stream().flatMap(q -> q.getQuestions().stream()).toList();
        var newQuestions = dslKit.getQuestions();

        Map<String, Question> savedQuestionCodesMap = savedQuestions.stream().collect(Collectors.toMap(Question::getCode, i -> i));
        Map<String, QuestionDslModel> newDslQuestionCodesMap = newQuestions.stream().collect(Collectors.toMap(QuestionDslModel::getCode, i -> i));

        List<String> sameLevels = sameCodesInNewDsl(savedQuestionCodesMap.keySet(), newDslQuestionCodesMap.keySet());

        boolean invalidateResults = false;
        for (String i : sameLevels) {
            boolean invalidOnUpdate = updateQuestion(savedQuestionCodesMap.get(i), newDslQuestionCodesMap.get(i), savedKit.getId());
            if (invalidOnUpdate)
                invalidateResults = true;
        }

        return new UpdateKitPersisterResult(invalidateResults);
    }

    private List<String> sameCodesInNewDsl(Set<String> savedItemCodes, Set<String> newItemCodes) {
        return savedItemCodes.stream()
            .filter(s -> newItemCodes.stream()
                .anyMatch(i -> i.equals(s)))
            .toList();
    }

    private boolean updateQuestion(Question savedQuestion, QuestionDslModel newDslQuestion, long kitId) {
        boolean invalidateResults = false;
        if (!savedQuestion.getTitle().equals(newDslQuestion.getTitle()) ||
            !savedQuestion.getHint().equals(newDslQuestion.getDescription()) ||
            savedQuestion.getIndex() != newDslQuestion.getIndex() ||
            savedQuestion.getMayNotBeApplicable() != newDslQuestion.isMayNotBeApplicable()) {
            var updateParam = new UpdateQuestionPort.Param(
                savedQuestion.getId(),
                newDslQuestion.getTitle(),
                newDslQuestion.getDescription(),
                newDslQuestion.getIndex(),
                newDslQuestion.isMayNotBeApplicable()
            );
            updateQuestionPort.update(updateParam);
            log.debug("A question with code [{}] is updated.", newDslQuestion.getCode());
            if (savedQuestion.getMayNotBeApplicable() != newDslQuestion.isMayNotBeApplicable()) {
                invalidateResults = true;
            }
        }

        if (savedQuestion.getImpacts() != null || newDslQuestion.getQuestionImpacts() != null) {
            invalidateResults = updateQuestionImpacts(savedQuestion, newDslQuestion, kitId, invalidateResults);
        }

        return invalidateResults;
    }

    private boolean updateQuestionImpacts(Question savedQuestion, QuestionDslModel newDslQuestion, long kitId, boolean invalidateResults) {
        return false;
    }
}
