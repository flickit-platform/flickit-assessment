package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.domain.dsl.*;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionsByQuestionPort;
import org.flickit.assessment.kit.application.port.out.answeroption.UpdateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.CreateAnswerOptionImpactPort;
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

import static java.util.stream.Collectors.*;
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

        Map<Long, String> savedAttributeIdToCodeMap = savedKit.getSubjects().stream()
            .flatMap(s -> s.getAttributes().stream()).collect(toMap(Attribute::getId, Attribute::getCode));
        Map<Long, String> savedLevelIdToCodeMap = savedKit.getMaturityLevels().stream().collect(toMap(MaturityLevel::getId, MaturityLevel::getCode));

        Set<String> savedQuestionnaireCodes = savedKit.getQuestionnaires().stream().map(Questionnaire::getCode).collect(toSet());
        Set<String> dslQuestionnaireCodes = dslKit.getQuestionnaires().stream().map(QuestionnaireDslModel::getCode).collect(toSet());

        // Map<questionnaireCode, Map<questionCode, question>>
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

        List<String> newQuestionnaireCodes = dslQuestionnaireCodes.stream()
            .filter(s -> !savedQuestionnaireCodes.contains(s))
            .toList();

        // Assuming that new questionnaires have been created in QuestionnairePersister
        newQuestionnaireCodes.forEach(code -> createQuestionsOfNewQuestionnaires(dslQuestionnaireToQuestionsMap.get(code),
            postUpdateQuestionnaires, postUpdateAttributes, postUpdateMaturityLevels));

        boolean invalidateResults = false;

        for (Map.Entry<String, Map<String, Question>> questionnaireEntry : savedQuestionnaireToQuestionsMap.entrySet()) {
            Map<String, Question> codeToQuestion = questionnaireEntry.getValue();
            Map<String, QuestionDslModel> codeToDslQuestion = dslQuestionnaireToQuestionsMap.get(questionnaireEntry.getKey());
            for (Map.Entry<String, Question> questionEntry : codeToQuestion.entrySet()) {
                Question question = questionEntry.getValue();
                QuestionDslModel dslQuestion = codeToDslQuestion.get(questionEntry.getKey());
                boolean invalidOnUpdate = updateQuestion(
                    question,
                    dslQuestion,
                    savedAttributeIdToCodeMap,
                    savedLevelIdToCodeMap,
                    postUpdateAttributes,
                    postUpdateMaturityLevels);
                if (invalidOnUpdate)
                    invalidateResults = true;
            }
        }
        return new UpdateKitPersisterResult(invalidateResults || !newQuestionnaireCodes.isEmpty());
    }

    private void createQuestionsOfNewQuestionnaires(Map<String, QuestionDslModel> dslQuestions,
                                                    Map<String, Long> questionnaires, Map<String, Long> attributes, Map<String, Long> maturityLevels) {
        if (dslQuestions == null || dslQuestions.isEmpty())
            return;

        dslQuestions.values().forEach(dslQuestion -> {
            var createParam = new CreateQuestionPort.Param(
                dslQuestion.getCode(),
                dslQuestion.getTitle(),
                dslQuestion.getDescription(),
                dslQuestion.getIndex(),
                questionnaires.get(dslQuestion.getQuestionnaireCode()),
                dslQuestion.isMayNotBeApplicable());
            Long questionId = createQuestionPort.persist(createParam);
            log.debug("Question[id={}, code={}, questionnaireCode={}] created.",
                questionId, dslQuestion.getCode(), questionnaires.get(dslQuestion.getQuestionnaireCode()));

            dslQuestion.getAnswerOptions().forEach(option -> createAnswerOption(option, questionId));

            dslQuestion.getQuestionImpacts().forEach(impact -> createImpact(impact, questionId, attributes, maturityLevels));
        });
    }

    private void createAnswerOption(AnswerOptionDslModel option, Long questionId) {
        var createOptionParam = new CreateAnswerOptionPort.Param(option.getCaption(), questionId, option.getIndex());
        var optionId = createAnswerOptionPort.persist(createOptionParam);
        log.debug("AnswerOption[Id={}, index={}, title={}, questionId={}] created.",
            optionId, option.getIndex(), option.getCaption(), questionId);
    }

    private void createImpact(QuestionImpactDslModel dslQuestionImpact, Long questionId,
                              Map<String, Long> attributes, Map<String, Long> maturityLevels) {
        QuestionImpact newQuestionImpact = new QuestionImpact(
            null,
            attributes.get(dslQuestionImpact.getAttributeCode()),
            maturityLevels.get(dslQuestionImpact.getMaturityLevel().getTitle()),
            dslQuestionImpact.getWeight(),
            questionId
        );
        Long impactId = createQuestionImpactPort.persist(newQuestionImpact);
        log.debug("QuestionImpact[impactId={}, questionId={}] created.", impactId, questionId);

        Map<Integer, Long> optionIndexToIdMap = loadAnswerOptionsByQuestionPort.loadByQuestionId(questionId).stream()
            .collect(toMap(AnswerOption::getIndex, AnswerOption::getId));

        dslQuestionImpact.getOptionsIndextoValueMap().keySet().forEach(
            index -> createAnswerOptionImpact(
                impactId,
                optionIndexToIdMap.get(index),
                dslQuestionImpact.getOptionsIndextoValueMap().get(index))
        );
    }

    private void createAnswerOptionImpact(Long questionImpactId, Long optionId, Double value) {
        var createParam = new CreateAnswerOptionImpactPort.Param(questionImpactId, optionId, value);
        Long optionImpactId = createAnswerOptionImpactPort.persist(createParam);
        log.debug("AnswerOptionImpact[id={}, questionImpactId={}, optionId={}] created.", optionImpactId, questionImpactId, optionId);
    }

    private boolean updateQuestion(Question savedQuestion, QuestionDslModel dslQuestion,
                                   Map<Long, String> savedAttributes, Map<Long, String> savedLevels,
                                   Map<String, Long> updatedAttributes, Map<String, Long> updatedLevels) {
        boolean invalidateResults = false;
        if (!savedQuestion.getTitle().equals(dslQuestion.getTitle()) ||
            !Objects.equals(savedQuestion.getHint(), dslQuestion.getDescription()) ||
            savedQuestion.getIndex() != dslQuestion.getIndex() ||
            !savedQuestion.getMayNotBeApplicable().equals(dslQuestion.isMayNotBeApplicable())) {
            var updateParam = new UpdateQuestionPort.Param(
                savedQuestion.getId(),
                dslQuestion.getTitle(),
                dslQuestion.getIndex(),
                dslQuestion.getDescription(),
                dslQuestion.isMayNotBeApplicable(),
                LocalDateTime.now()
            );
            updateQuestionPort.update(updateParam);
            log.debug("Question[id={}] updated.", savedQuestion.getId());
            if (!savedQuestion.getMayNotBeApplicable().equals(dslQuestion.isMayNotBeApplicable())) {
                invalidateResults = true;
            }
        }

        updateAnswerOptions(savedQuestion, dslQuestion);
        boolean invalidOnUpdateQuestionImpact = updateQuestionImpacts(savedQuestion, dslQuestion, savedAttributes, savedLevels, updatedAttributes, updatedLevels);

        return invalidateResults || invalidOnUpdateQuestionImpact;
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
                                          Map<Long, String> savedAttributes, Map<Long, String> savedLevels,
                                          Map<String, Long> updatedAttributes, Map<String, Long> updatedLevels) {
        Map<AttributeLevel, QuestionImpact> savedImpactsMap = savedQuestion.getImpacts().stream()
            .collect(toMap(impact -> createSavedAttributeLevel(impact, savedAttributes, savedLevels), i -> i));
        Map<AttributeLevel, QuestionImpactDslModel> dslImpactMap = dslQuestion.getQuestionImpacts().stream()
            .collect(toMap(i -> new AttributeLevel(i.getAttributeCode(), i.getMaturityLevel().getTitle()), i -> i));

        List<AttributeLevel> newImpacts = newImpactsInNewDsl(savedImpactsMap.keySet(), dslImpactMap.keySet());
        List<AttributeLevel> deletedImpacts = deletedImpactsInNewDsl(savedImpactsMap.keySet(), dslImpactMap.keySet());
        List<AttributeLevel> sameImpacts = sameImpactsInNewDsl(savedImpactsMap.keySet(), dslImpactMap.keySet());

        newImpacts.forEach(i -> createImpact(dslImpactMap.get(i), savedQuestion.getId(), updatedAttributes, updatedLevels));
        deletedImpacts.forEach(i -> deleteImpact(savedImpactsMap.get(i), savedQuestion.getId()));

        boolean invalidOnUpdate = false;
        for (AttributeLevel impact : sameImpacts)
            invalidOnUpdate = updateImpact(savedQuestion, savedImpactsMap.get(impact), dslImpactMap.get(impact));

        return !newImpacts.isEmpty() || !deletedImpacts.isEmpty() || invalidOnUpdate;
    }

    private AttributeLevel createSavedAttributeLevel(QuestionImpact impact, Map<Long, String> attributes, Map<Long, String> maturityLevels) {
        String attributeCode = attributes.get(impact.getAttributeId());
        String levelCode = maturityLevels.get(impact.getMaturityLevelId());
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

    private void deleteImpact(QuestionImpact impact, Long questionId) {
        deleteQuestionImpactPort.delete(impact.getId());
        log.debug("QuestionImpact[id={}, questionId={}] deleted.", impact.getId(), questionId);
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
            log.debug("QuestionImpact[id={}, questionId={}] updated.", savedImpact.getId(), savedQuestion.getId());
            invalidateResult = true;
        }

        boolean invalidateOnUpdateOptionImpact = updateOptionImpacts(savedQuestion, savedImpact, dslImpact);

        return invalidateResult || invalidateOnUpdateOptionImpact;
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
            .filter(o -> o.getIndex() == index)
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
