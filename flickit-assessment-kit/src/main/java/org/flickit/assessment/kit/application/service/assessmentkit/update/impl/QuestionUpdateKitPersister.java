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
import org.flickit.assessment.kit.application.port.out.questionimpact.UpdateQuestionImpactByDslPort;
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
    private final UpdateQuestionImpactByDslPort updateQuestionImpactByDslPort;
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
    public UpdateKitPersisterResult persist(UpdateKitPersisterContext ctx,
                                            AssessmentKit savedKit,
                                            AssessmentKitDslModel dslKit,
                                            UUID currentUserId) {
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
        newQuestionnaireCodes.forEach(code -> createQuestions(dslQuestionnaireToQuestionsMap.get(code),
            postUpdateQuestionnaires, postUpdateAttributes, postUpdateMaturityLevels, savedKit.getActiveVersionId(), currentUserId));

        boolean isMajorUpdate = false;

        for (Map.Entry<String, Map<String, Question>> questionnaireEntry : savedQuestionnaireToQuestionsMap.entrySet()) {
            Map<String, Question> codeToQuestion = questionnaireEntry.getValue();
            Map<String, QuestionDslModel> codeToDslQuestion = dslQuestionnaireToQuestionsMap.get(questionnaireEntry.getKey());
            for (Map.Entry<String, Question> questionEntry : codeToQuestion.entrySet()) {
                Question question = questionEntry.getValue();
                QuestionDslModel dslQuestion = codeToDslQuestion.get(questionEntry.getKey());
                boolean isKitModificationMajor = updateQuestion(
                    question,
                    savedKit.getActiveVersionId(), dslQuestion,
                    savedAttributeIdToCodeMap,
                    savedLevelIdToCodeMap,
                    postUpdateAttributes,
                    postUpdateMaturityLevels,
                    currentUserId);
                if (isKitModificationMajor)
                    isMajorUpdate = true;
            }
        }

        boolean haveNewQuestionsBeenAdded = haveNewQuestionsBeenAdded(
            savedQuestionnaireToQuestionsMap,
            dslQuestionnaireToQuestionsMap,
            postUpdateQuestionnaires,
            postUpdateAttributes,
            postUpdateMaturityLevels,
            savedKit.getActiveVersionId(),
            currentUserId);

        isMajorUpdate = isMajorUpdate || haveNewQuestionsBeenAdded;

        return new UpdateKitPersisterResult(isMajorUpdate || !newQuestionnaireCodes.isEmpty());
    }

    private boolean haveNewQuestionsBeenAdded(Map<String, Map<String, Question>> savedQuestionnaireToQuestionsMap,
                                              Map<String, Map<String, QuestionDslModel>> dslQuestionnaireToQuestionsMap,
                                              Map<String, Long> postUpdateQuestionnaires,
                                              Map<String, Long> postUpdateAttributes,
                                              Map<String, Long> postUpdateMaturityLevels,
                                              long kitVersionId,
                                              UUID currentUserId) {
        boolean questionAddition = false;
        for (Map.Entry<String, Map<String, Question>> questionnaire : savedQuestionnaireToQuestionsMap.entrySet()) {
            Map<String, Question> savedQuestions = questionnaire.getValue();
            Map<String, QuestionDslModel> dslQuestions = dslQuestionnaireToQuestionsMap.get(questionnaire.getKey());

            if (dslQuestions == null || dslQuestions.isEmpty())
                continue;

            Map<String, QuestionDslModel> newDslQuestionsMap = dslQuestions.entrySet().stream()
                .filter(e -> !savedQuestions.containsKey(e.getKey()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

            createQuestions(newDslQuestionsMap, postUpdateQuestionnaires, postUpdateAttributes, postUpdateMaturityLevels, kitVersionId, currentUserId);

            if (!newDslQuestionsMap.isEmpty())
                questionAddition = true;
        }
        return questionAddition;
    }

    private void createQuestions(Map<String, QuestionDslModel> dslQuestions,
                                    Map<String, Long> questionnaires,
                                    Map<String, Long> attributes,
                                    Map<String, Long> maturityLevels,
                                    long kitVersionId,
                                    UUID currentUserId) {
        if (dslQuestions == null || dslQuestions.isEmpty())
            return;

        dslQuestions.values().forEach(dslQuestion -> {
            Long questionnaireId = questionnaires.get(dslQuestion.getQuestionnaireCode());
            var createParam = toCreateQuestionParam(kitVersionId, questionnaireId, currentUserId, dslQuestion);

            Long questionId = createQuestionPort.persist(createParam);
            log.debug("Question[id={}, code={}, questionnaireCode={}] created.",
                questionId, dslQuestion.getCode(), questionnaires.get(dslQuestion.getQuestionnaireCode()));

            dslQuestion.getAnswerOptions().forEach(option -> createAnswerOption(option, questionId, kitVersionId, currentUserId));

            dslQuestion.getQuestionImpacts().forEach(impact ->
                createImpact(impact, kitVersionId, questionId, attributes, maturityLevels, currentUserId));
        });
    }

    private CreateQuestionPort.Param toCreateQuestionParam(Long kitVersionId, Long questionnaireId, UUID currentUserId, QuestionDslModel dslQuestion) {
        return new CreateQuestionPort.Param(
            dslQuestion.getCode(),
            dslQuestion.getTitle(),
            dslQuestion.getIndex(),
            dslQuestion.getDescription(),
            dslQuestion.isMayNotBeApplicable(),
            dslQuestion.isAdvisable(),
            kitVersionId,
            questionnaireId,
            currentUserId);
    }

    private void createAnswerOption(AnswerOptionDslModel option, Long questionId, long kitVersionId, UUID currentUserId) {
        var createOptionParam =
            new CreateAnswerOptionPort.Param(option.getCaption(), option.getIndex(), questionId, kitVersionId, currentUserId);
        var optionId = createAnswerOptionPort.persist(createOptionParam);
        log.debug("AnswerOption[Id={}, index={}, title={}, questionId={}] created.",
            optionId, option.getIndex(), option.getCaption(), questionId);
    }

    private void createImpact(QuestionImpactDslModel dslQuestionImpact,
                              Long kitVersionId,
                              Long questionId,
                              Map<String, Long> attributes,
                              Map<String, Long> maturityLevels,
                              UUID currentUserId) {
        QuestionImpact newQuestionImpact = new QuestionImpact(
            null,
            attributes.get(dslQuestionImpact.getAttributeCode()),
            maturityLevels.get(dslQuestionImpact.getMaturityLevel().getCode()),
            dslQuestionImpact.getWeight(),
            kitVersionId,
            questionId,
            LocalDateTime.now(),
            LocalDateTime.now(),
            currentUserId,
            currentUserId
        );
        Long impactId = createQuestionImpactPort.persist(newQuestionImpact);
        log.debug("QuestionImpact[impactId={}, questionId={}] created.", impactId, questionId);

        Map<Integer, Long> optionIndexToIdMap = loadAnswerOptionsByQuestionPort.loadByQuestionId(questionId, kitVersionId).stream()
            .collect(toMap(AnswerOption::getIndex, AnswerOption::getId));

        dslQuestionImpact.getOptionsIndextoValueMap().keySet().forEach(
            index -> createAnswerOptionImpact(
                impactId,
                optionIndexToIdMap.get(index),
                dslQuestionImpact.getOptionsIndextoValueMap().get(index),
                kitVersionId,
                currentUserId)
        );
    }

    private void createAnswerOptionImpact(Long questionImpactId, Long optionId, Double value, Long kitVersionId, UUID currentUserId) {
        var createParam = new CreateAnswerOptionImpactPort.Param(questionImpactId, optionId, value, kitVersionId, currentUserId);
        Long optionImpactId = createAnswerOptionImpactPort.persist(createParam);
        log.debug("AnswerOptionImpact[id={}, questionImpactId={}, optionId={}] created.", optionImpactId, questionImpactId, optionId);
    }

    private boolean updateQuestion(Question savedQuestion,
                                   Long kitVersionId,
                                   QuestionDslModel dslQuestion,
                                   Map<Long, String> savedAttributes,
                                   Map<Long, String> savedLevels,
                                   Map<String, Long> updatedAttributes,
                                   Map<String, Long> updatedLevels,
                                   UUID currentUserId) {
        boolean isMajorUpdate = false;
        if (!savedQuestion.getTitle().equals(dslQuestion.getTitle()) ||
            !Objects.equals(savedQuestion.getHint(), dslQuestion.getDescription()) ||
            savedQuestion.getIndex() != dslQuestion.getIndex() ||
            !savedQuestion.getMayNotBeApplicable().equals(dslQuestion.isMayNotBeApplicable()) ||
            !savedQuestion.getAdvisable().equals(dslQuestion.isAdvisable())) {
            var updateParam = new UpdateQuestionPort.Param(
                savedQuestion.getId(),
                kitVersionId,
                dslQuestion.getTitle(),
                dslQuestion.getIndex(),
                dslQuestion.getDescription(),
                dslQuestion.isMayNotBeApplicable(),
                dslQuestion.isAdvisable(),
                LocalDateTime.now(),
                currentUserId
            );
            updateQuestionPort.update(updateParam);
            log.debug("Question[id={}] updated.", savedQuestion.getId());
            if (!savedQuestion.getMayNotBeApplicable().equals(dslQuestion.isMayNotBeApplicable())) {
                isMajorUpdate = true;
            }
        }

        updateAnswerOptions(savedQuestion, dslQuestion, kitVersionId, currentUserId);
        boolean isMajorUpdateQuestionImpact = updateQuestionImpacts(savedQuestion,
            kitVersionId,
            dslQuestion,
            savedAttributes,
            savedLevels,
            updatedAttributes,
            updatedLevels,
            currentUserId);

        return isMajorUpdate || isMajorUpdateQuestionImpact;
    }

    private void updateAnswerOptions(Question savedQuestion, QuestionDslModel dslQuestion, Long kitVersionId, UUID currentUserId) {
        Map<Integer, AnswerOption> savedOptionIndexMap = savedQuestion.getOptions().stream()
            .collect(toMap(AnswerOption::getIndex, a -> a));

        Map<Integer, AnswerOptionDslModel> dslOptionIndexMap = dslQuestion.getAnswerOptions().stream()
            .collect(toMap(AnswerOptionDslModel::getIndex, a -> a));

        for (Map.Entry<Integer, AnswerOption> optionEntry : savedOptionIndexMap.entrySet()) {
            String savedOptionTitle = optionEntry.getValue().getTitle();
            String dslOptionTitle = dslOptionIndexMap.get(optionEntry.getKey()).getCaption();
            if (!savedOptionTitle.equals(dslOptionTitle)) {
                updateAnswerOptionPort.update(new UpdateAnswerOptionPort.Param(optionEntry.getValue().getId(),
                    kitVersionId, dslOptionTitle, LocalDateTime.now(), currentUserId));
                log.debug("AnswerOption[id={}, index={}, newTitle{}, questionId{}] updated.",
                    optionEntry.getValue().getId(), optionEntry.getKey(), dslOptionTitle, savedQuestion.getId());
            }
        }
    }

    private record AttributeLevel(String attributeCode, String levelCode) {
    }

    private boolean updateQuestionImpacts(Question savedQuestion,
                                          Long kitVersionId,
                                          QuestionDslModel dslQuestion,
                                          Map<Long, String> savedAttributes,
                                          Map<Long, String> savedLevels,
                                          Map<String, Long> updatedAttributes,
                                          Map<String, Long> updatedLevels,
                                          UUID currentUserId) {
        Map<AttributeLevel, QuestionImpact> savedImpactsMap = savedQuestion.getImpacts().stream()
            .collect(toMap(impact -> createSavedAttributeLevel(impact, savedAttributes, savedLevels), i -> i));
        Map<AttributeLevel, QuestionImpactDslModel> dslImpactMap = dslQuestion.getQuestionImpacts().stream()
            .collect(toMap(i -> new AttributeLevel(i.getAttributeCode(), i.getMaturityLevel().getCode()), i -> i));

        List<AttributeLevel> newImpacts = newImpactsInNewDsl(savedImpactsMap.keySet(), dslImpactMap.keySet());
        List<AttributeLevel> deletedImpacts = deletedImpactsInNewDsl(savedImpactsMap.keySet(), dslImpactMap.keySet());
        List<AttributeLevel> sameImpacts = sameImpactsInNewDsl(savedImpactsMap.keySet(), dslImpactMap.keySet());

        newImpacts.forEach(
            i -> createImpact(dslImpactMap.get(i),
                kitVersionId,
                savedQuestion.getId(),
                updatedAttributes,
                updatedLevels,
                currentUserId));
        deletedImpacts.forEach(i -> deleteImpact(savedImpactsMap.get(i), savedQuestion.getId()));

        boolean isMajorUpdate = false;
        for (AttributeLevel impact : sameImpacts)
            isMajorUpdate = updateImpact(savedQuestion,
                savedImpactsMap.get(impact),
                dslImpactMap.get(impact),
                currentUserId);

        return !newImpacts.isEmpty() || !deletedImpacts.isEmpty() || isMajorUpdate;
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
        deleteQuestionImpactPort.delete(impact.getId(), impact.getKitVersionId());
        log.debug("QuestionImpact[id={}, questionId={}] deleted.", impact.getId(), questionId);
    }

    private boolean updateImpact(Question savedQuestion,
                                 QuestionImpact savedImpact,
                                 QuestionImpactDslModel dslImpact,
                                 UUID currentUserId) {
        boolean isMajorUpdate = false;
        if (savedImpact.getWeight() != dslImpact.getWeight()) {
            var updateParam = new UpdateQuestionImpactByDslPort.Param(
                savedImpact.getId(),
                savedImpact.getKitVersionId(),
                dslImpact.getWeight(),
                savedImpact.getQuestionId(),
                LocalDateTime.now(),
                currentUserId
            );
            updateQuestionImpactByDslPort.updateByDsl(updateParam);
            log.debug("QuestionImpact[id={}, questionId={}] updated.", savedImpact.getId(), savedQuestion.getId());
            isMajorUpdate = true;
        }

        boolean isMajorUpdateOptionImpact = updateOptionImpacts(savedQuestion, savedImpact, dslImpact, currentUserId);

        return isMajorUpdate || isMajorUpdateOptionImpact;
    }

    private boolean updateOptionImpacts(Question savedQuestion,
                                        QuestionImpact savedImpact,
                                        QuestionImpactDslModel dslImpact,
                                        UUID currentUserId) {
        boolean isMajorUpdate = false;
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
                updateAnswerOptionImpact(savedImpact.getKitVersionId(), savedOptionImpact, newOptionImpact, currentUserId);
                isMajorUpdate = true;
            }
        }
        return isMajorUpdate;
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

    private void updateAnswerOptionImpact(long kitVersionId,
                                          AnswerOptionImpact savedOptionImpact,
                                          AnswerOptionImpact dslOptionImpact,
                                          UUID currentUserId) {
        var updateParam = new UpdateAnswerOptionImpactPort.Param(
            savedOptionImpact.getId(),
            kitVersionId,
            dslOptionImpact.getValue(),
            LocalDateTime.now(),
            currentUserId
        );
        updateAnswerOptionImpactPort.update(updateParam);
        log.debug("AnswerOptionImpact[id={}, optionId={}, newValue={}] updated.",
            savedOptionImpact.getId(), savedOptionImpact.getOptionId(), dslOptionImpact.getValue());
    }
}
