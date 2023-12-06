package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.domain.dsl.*;
import org.flickit.assessment.kit.application.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionsByQuestionPort;
import org.flickit.assessment.kit.application.port.out.answeroption.UpdateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.CreateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.DeleteAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.UpdateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.question.CreateQuestionPort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.CreateQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.DeleteQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.UpdateQuestionImpactPort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext.*;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_KIT_BY_DSL_ANSWER_OPTION_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionUpdateKitPersister implements UpdateKitPersister {

    private final UpdateQuestionPort updateQuestionPort;
    private final CreateQuestionPort createQuestionPort;
    private final CreateQuestionImpactPort createQuestionImpactPort;
    private final DeleteQuestionImpactPort deleteQuestionImpactPort;
    private final UpdateQuestionImpactPort updateQuestionImpactPort;
    private final CreateAnswerOptionImpactPort createAnswerOptionImpactPort;
    private final DeleteAnswerOptionImpactPort deleteAnswerOptionImpactPort;
    private final UpdateAnswerOptionImpactPort updateAnswerOptionImpactPort;
    private final UpdateAnswerOptionPort updateAnswerOptionPort;
    private final LoadAnswerOptionsByQuestionPort loadAnswerOptionsByQuestionPort;
    private final CreateAnswerOptionPort createAnswerOptionPort;

    @Override
    public int order() {
        return 5;
    }

    @Override
    public UpdateKitPersisterResult persist(UpdateKitPersisterContext ctx, AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        Map<String, Long> postUpdateQuestionnaires = ctx.get(KEY_QUESTIONNAIRES);
        Map<String, Long> postUpdateAttributes = ctx.get(KEY_ATTRIBUTES);
        Map<String, Long> postUpdateMaturityLevels = ctx.get(KEY_MATURITY_LEVELS);

        var savedQuestionnaires = savedKit.getQuestionnaires();
        var dslQuestionnaires = dslKit.getQuestionnaires();

        Map<String, Questionnaire> savedQuestionnaireCodesMap = savedQuestionnaires.stream().collect(toMap(Questionnaire::getCode, q -> q));
        Map<String, QuestionnaireDslModel> dslQuestionnaireCodesMap = dslQuestionnaires.stream().collect(toMap(QuestionnaireDslModel::getCode, q -> q));

        List<String> newQuestionnaires = dslQuestionnaireCodesMap.keySet().stream()
            .filter(s -> !savedQuestionnaireCodesMap.containsKey(s))
            .toList();

        // Assuming that new questionnaires have been created in Questionnaire persister
        createQuestion(dslKit.getQuestions(), newQuestionnaires,
            postUpdateQuestionnaires, postUpdateAttributes, postUpdateMaturityLevels);

        List<String> sameQuestionnaires = savedQuestionnaireCodesMap.keySet().stream()
            .filter(dslQuestionnaireCodesMap::containsKey)
            .toList();

        boolean invalidateResults = false;
        for (String questionnaireCode : sameQuestionnaires) {
            Questionnaire questionnaire = savedQuestionnaireCodesMap.get(questionnaireCode);
            if (Objects.nonNull(questionnaire.getQuestions())) {
                var savedQuestions = questionnaire.getQuestions();
                var dslQuestions = dslKit.getQuestions().stream().filter(i -> i.getQuestionnaireCode().equals(questionnaireCode)).toList();

                Map<String, Question> savedQuestionCodesMap = savedQuestions.stream().collect(toMap(Question::getCode, i -> i));
                Map<String, QuestionDslModel> dslQuestionCodesMap = dslQuestions.stream().collect(toMap(QuestionDslModel::getCode, i -> i));

                List<String> sameLevels = sameCodesInNewDsl(savedQuestionCodesMap.keySet(), dslQuestionCodesMap.keySet());

                for (String i : sameLevels) {
                    boolean invalidOnUpdate = updateQuestion(savedQuestionCodesMap.get(i), dslQuestionCodesMap.get(i),
                        postUpdateAttributes, postUpdateMaturityLevels);
                    if (invalidOnUpdate)
                        invalidateResults = true;
                }
            }
        }

        return new UpdateKitPersisterResult(invalidateResults || !newQuestionnaires.isEmpty());
    }

    private void createQuestion(List<QuestionDslModel> dslQuestions, List<String> newQuestionnaires,
                                Map<String, Long> questionnaires, Map<String, Long> attributes, Map<String, Long> maturityLevels) {
        newQuestionnaires.forEach(q -> {
            var newQuestions = dslQuestions.stream().filter(i -> i.getQuestionnaireCode().equals(q)).toList();
            if (Objects.nonNull(newQuestions) && !newQuestions.isEmpty()) {
                newQuestions.forEach(i -> {
                    var createParam = new CreateQuestionPort.Param(
                        i.getCode(),
                        i.getTitle(),
                        i.getDescription(),
                        i.getIndex(),
                        questionnaires.get(q),
                        i.isMayNotBeApplicable());
                    Long questionId = createQuestionPort.persist(createParam);
                    log.warn("Question with id [{}] is created.", questionId);

                    var newOptions = i.getAnswerOptions();
                    if (Objects.nonNull(newOptions) && !newOptions.isEmpty()) {
                        newOptions.forEach(n -> createAnswerOption(n, questionId));
                    }

                    var newImpacts = i.getQuestionImpacts();
                    if (Objects.nonNull(newImpacts) && !newImpacts.isEmpty()) {
                        newImpacts.forEach(n -> createImpact(questionId, n, attributes, maturityLevels));
                    }
                });
            }
        });
    }

    private void createAnswerOption(AnswerOptionDslModel n, Long questionId) {
        var createOptionParam = new CreateAnswerOptionPort.Param(n.getCaption(), questionId, n.getIndex());
        var optionId = createAnswerOptionPort.persist(createOptionParam);
        log.warn("Answer option with id [{}] is created.", optionId);
    }

    private List<String> sameCodesInNewDsl(Set<String> savedItemCodes, Set<String> newItemCodes) {
        return savedItemCodes.stream()
            .filter(s -> newItemCodes.stream()
                .anyMatch(i -> i.equals(s)))
            .toList();
    }

    private boolean updateQuestion(Question savedQuestion, QuestionDslModel dslQuestion, Map<String, Long> attributes, Map<String, Long> maturityLevels) {
        boolean invalidateResults = false;
        if (!savedQuestion.getTitle().equals(dslQuestion.getTitle()) ||
            !Objects.equals(savedQuestion.getHint(), dslQuestion.getDescription()) ||
            savedQuestion.getIndex() != dslQuestion.getIndex() ||
            savedQuestion.getMayNotBeApplicable() != dslQuestion.isMayNotBeApplicable()) {
            var updateParam = new UpdateQuestionPort.Param(
                savedQuestion.getId(),
                dslQuestion.getTitle(),
                dslQuestion.getIndex(),
                dslQuestion.getDescription(),
                dslQuestion.isMayNotBeApplicable(),
                LocalDateTime.now()
            );
            updateQuestionPort.update(updateParam);
            log.warn("A question with id [{}] is updated.", savedQuestion.getId());
            if (savedQuestion.getMayNotBeApplicable() != dslQuestion.isMayNotBeApplicable()) {
                invalidateResults = true;
            }
        }

        if (savedQuestion.getOptions() != null || dslQuestion.getAnswerOptions() != null) {
            invalidateResults = invalidateResults || updateAnswerOptions(savedQuestion, dslQuestion);
        }

        if (savedQuestion.getImpacts() != null || dslQuestion.getQuestionImpacts() != null) {
            invalidateResults = invalidateResults || updateQuestionImpacts(savedQuestion, dslQuestion, attributes, maturityLevels);
        }

        return invalidateResults;
    }

    private void updateAnswerOptions(Question savedQuestion, QuestionDslModel dslQuestion) {
        Map<Integer, AnswerOption> savedOptionIndexMap = savedQuestion.getOptions().stream()
            .collect(toMap(AnswerOption::getIndex, a -> a));

        Map<Integer, AnswerOptionDslModel> dslOptionIndexMap = dslQuestion.getAnswerOptions().stream()
            .collect(toMap(AnswerOptionDslModel::getIndex, a -> a));

        for (Map.Entry<Integer, AnswerOption> optionEntry : savedOptionIndexMap.entrySet()) {
            String savedOptionTitle = optionEntry.getValue().getTitle();
            String dslOptionTitle = dslOptionIndexMap.get(optionEntry.getKey()).getCaption();
            if (!savedOptionTitle.equals(dslOptionTitle)) {
                updateAnswerOptionPort.update(new UpdateAnswerOptionPort.Param(optionEntry.getValue().getId(), dslOptionTitle));
                log.debug("AnswerOption[id={}, index={}, newTitle{}, questionId{}] updated.",
                    optionEntry.getValue().getId(), optionEntry.getKey(), dslOptionTitle, savedQuestion.getId());
            }
        }
    }

    private record AttributeLevel(String attributeCode, String levelCode) {
    }

    private boolean updateQuestionImpacts(Question savedQuestion, QuestionDslModel dslQuestion,
                                          Map<String, Long> attributes, Map<String, Long> maturityLevels) {
        Map<AttributeLevel, QuestionImpact> savedImpactsMap = savedQuestion.getImpacts().stream()
            .collect(toMap(impact -> createAttributeLevel(impact, attributes, maturityLevels), i -> i));
        Map<AttributeLevel, QuestionImpactDslModel> dslImpactMap = dslQuestion.getQuestionImpacts().stream()
            .collect(toMap(i -> new AttributeLevel(i.getAttributeCode(), i.getMaturityLevel().getCode()), i -> i));

        List<AttributeLevel> newImpacts = newImpactsInNewDsl(savedImpactsMap.keySet(), dslImpactMap.keySet());
        List<AttributeLevel> deletedImpacts = deletedImpactsInNewDsl(savedImpactsMap.keySet(), dslImpactMap.keySet());
        List<AttributeLevel> sameImpacts = sameImpactsInNewDsl(savedImpactsMap.keySet(), dslImpactMap.keySet());

        newImpacts.forEach(i -> createImpact(dslImpactMap.get(i), savedQuestion.getId(), attributes, maturityLevels));
        deletedImpacts.forEach(i -> deleteImpact(savedImpactsMap.get(i), savedQuestion.getId()));

        boolean invalidOnUpdate = false;
        for (AttributeLevel impact : sameImpacts)
            invalidOnUpdate = updateImpact(savedQuestion, savedImpactsMap.get(impact), dslImpactMap.get(impact));

        return !newImpacts.isEmpty() || !deletedImpacts.isEmpty() || invalidOnUpdate;
    }

    private AttributeLevel createAttributeLevel(QuestionImpact impact, Map<String, Long> attributes, Map<String, Long> maturityLevels) {
        Map<Long, String> attributesIdToCode = attributes.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        Map<Long, String> levelsIdToCode = maturityLevels.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        String attributeCode = attributesIdToCode.get(impact.getAttributeId());
        String levelCode = levelsIdToCode.get(impact.getMaturityLevelId());
        return new AttributeLevel(attributeCode, levelCode);
    }

    private List<AttributeLevel> newImpactsInNewDsl(Set<AttributeLevel> savedImpactCodes, Set<AttributeLevel> dslImpactCodes) {
        return dslImpactCodes.stream()
            .filter(i -> savedImpactCodes.stream()
                .noneMatch(s -> s.attributeCode.equals(i.attributeCode) && s.levelCode.equals(i.levelCode)))
            .toList();
    }

    private List<AttributeLevel> deletedImpactsInNewDsl(Set<AttributeLevel> savedImpactCodes, Set<AttributeLevel> dslImpactCodes) {
        return savedImpactCodes.stream()
            .filter(i -> dslImpactCodes.stream()
                .noneMatch(s -> s.attributeCode.equals(i.attributeCode) && s.levelCode.equals(i.levelCode)))
            .toList();
    }

    private List<AttributeLevel> sameImpactsInNewDsl(Set<AttributeLevel> savedImpactCodes, Set<AttributeLevel> dslImpactCodes) {
        return savedImpactCodes.stream()
            .filter(i -> dslImpactCodes.stream()
                .anyMatch(s -> s.attributeCode.equals(i.attributeCode) && s.levelCode().equals(i.levelCode())))
            .toList();
    }

    private void createImpact(Long questionId, QuestionImpactDslModel dslQuestionImpact,
                              Map<String, Long> attributes, Map<String, Long> maturityLevels) {
        QuestionImpact newQuestionImpact = new QuestionImpact(
            null,
            attributes.get(dslQuestionImpact.getAttributeCode()),
            maturityLevels.get(dslQuestionImpact.getMaturityLevel().getTitle()),
            dslQuestionImpact.getWeight(),
            questionId
        );
        Long impactId = createQuestionImpactPort.persist(newQuestionImpact);
        log.warn("Question impact with is [{}] is created.", impactId);

        Map<Integer, Long> answerOptionIndexToIdMap = loadAnswerOptionsByQuestionPort.loadByQuestionId(questionId).stream()
            .collect(toMap(AnswerOption::getIndex, AnswerOption::getQuestionId));
        dslQuestionImpact.getOptionsIndextoValueMap().keySet().forEach(index -> createAnswerOptionImpact(
            impactId,
            answerOptionIndexToIdMap.get(index),
            dslQuestionImpact.getOptionsIndextoValueMap().get(index))
        );
    }

    private void deleteImpact(QuestionImpact impact) {
        deleteQuestionImpactPort.delete(impact.getId());
        log.warn("Question impact with id [{}] is deleted.", impact.getId());

        impact.getOptionImpacts().forEach(o -> {
            deleteAnswerOptionImpactPort.delete(impact.getId(), o.getOptionId());
            log.warn("Answer option impact with question impact id [{}], option id [{}] and value [{}] is deleted.",
                impact.getId(), o.getOptionId(), o.getValue());
        });
    }

    private boolean updateImpact(Question savedQuestion, QuestionImpact savedImpact, QuestionImpactDslModel dslImpact) {
        boolean invalidateResult = false;
        if (savedImpact.getWeight() != dslImpact.getWeight()) {
            var updateParam = new UpdateQuestionImpactPort.Param(
                savedImpact.getId(),
                dslImpact.getWeight(),
                savedImpact.getQuestionId()
            );
            updateQuestionImpactPort.update(updateParam);
            log.warn("Question impact with id [{}] is updated.", savedImpact.getId());
            invalidateResult = true;
        }

        if (savedImpact.getOptionImpacts() != null || dslImpact.getOptionsIndextoValueMap() != null) {
            invalidateResult = invalidateResult || updateOptionImpacts(savedQuestion, savedImpact, dslImpact);
        }

        return invalidateResult;
    }

    private boolean updateOptionImpacts(Question savedQuestion, QuestionImpact savedImpact, QuestionImpactDslModel dslImpact) {
        boolean invalidateResults = false;
        Map<Long, AnswerOption> optionMap = savedQuestion.getOptions().stream().collect(toMap(AnswerOption::getId, i -> i));

        Map<Integer, AnswerOptionImpact> savedOptionImpactMap = savedImpact.getOptionImpacts().stream()
            .collect(toMap(a -> optionMap.get(a.getOptionId()).getIndex(), a -> a));

        Map<Integer, AnswerOptionImpact> dslOptionImpactMap = dslImpact.getOptionsIndextoValueMap().entrySet().stream()
            .collect(toMap(Map.Entry::getKey,
                entry -> buildOptionImpact(savedQuestion, entry.getKey(), dslImpact.getOptionsIndextoValueMap().get(entry.getKey()))));

        for (Map.Entry<Integer, AnswerOptionImpact> entry : savedOptionImpactMap.entrySet()) {
            AnswerOptionImpact savedOptionImpact = entry.getValue();
            AnswerOptionImpact newOptionImpact = dslOptionImpactMap.get(entry.getKey());

            if (savedOptionImpact.getValue() != newOptionImpact.getValue()) {
                updateAnswerOptionImpact(savedOptionImpact, newOptionImpact);
                invalidateResults = true;
            }
        }
        return invalidateResults;
    }

    private AnswerOptionImpact buildOptionImpact(Question savedQuestion, Integer index, Double value) {
        Optional<AnswerOption> answerOption = savedQuestion.getOptions().stream()
            .filter(o -> o.getQuestionId() == savedQuestion.getId() && o.getIndex() == index)
            .findFirst();
        return new AnswerOptionImpact(
            null,
            answerOption.orElseThrow(() -> new ResourceNotFoundException(UPDATE_KIT_BY_DSL_ANSWER_OPTION_NOT_FOUND)).getId(),
            value
        );
    }

    private void updateAnswerOptionImpact(AnswerOptionImpact savedOptionImpact, AnswerOptionImpact dslOptionImpact) {
        var updateParam = new UpdateAnswerOptionImpactPort.Param(
            savedOptionImpact.getId(),
            dslOptionImpact.getValue()
        );
        updateAnswerOptionImpactPort.update(updateParam);
        log.debug("AnswerOptionImpact[id={}, optionId={}, newValue={}] updated.",
            savedOptionImpact.getId(), savedOptionImpact.getOptionId(), dslOptionImpact.getValue());
    }
}
