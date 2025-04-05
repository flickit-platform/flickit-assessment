package org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.domain.dsl.*;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answeroption.UpdateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answerrange.CreateAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.question.CreateQuestionPort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.CreateQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.DeleteQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.UpdateQuestionImpactPort;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.*;
import static org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionUpdateKitPersister implements UpdateKitPersister {

    private final UpdateQuestionPort updateQuestionPort;
    private final CreateQuestionPort createQuestionPort;
    private final CreateQuestionImpactPort createQuestionImpactPort;
    private final DeleteQuestionImpactPort deleteQuestionImpactPort;
    private final UpdateQuestionImpactPort updateQuestionImpactPort;
    private final UpdateAnswerOptionPort updateAnswerOptionPort;
    private final CreateAnswerOptionPort createAnswerOptionPort;
    private final CreateAnswerRangePort createAnswerRangePort;

    @Override
    public int order() {
        return 7;
    }

    @Override
    public UpdateKitPersisterResult persist(UpdateKitPersisterContext ctx,
                                            AssessmentKit savedKit,
                                            AssessmentKitDslModel dslKit,
                                            UUID currentUserId) {
        Map<String, Long> postUpdateQuestionnaires = ctx.get(KEY_QUESTIONNAIRES);
        Map<String, Long> postUpdateMeasures = ctx.get(KEY_MEASURE);
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
            postUpdateQuestionnaires,
            postUpdateMeasures,
            postUpdateAttributes,
            postUpdateMaturityLevels,
            savedKit.getActiveVersionId(),
            currentUserId));

        boolean isMajorUpdate = false;

        for (Map.Entry<String, Map<String, Question>> questionnaireEntry : savedQuestionnaireToQuestionsMap.entrySet()) {
            Map<String, Question> codeToQuestion = questionnaireEntry.getValue();
            Map<String, QuestionDslModel> codeToDslQuestion = dslQuestionnaireToQuestionsMap.get(questionnaireEntry.getKey());
            for (Map.Entry<String, Question> questionEntry : codeToQuestion.entrySet()) {
                Question question = questionEntry.getValue();
                QuestionDslModel dslQuestion = codeToDslQuestion.get(questionEntry.getKey());
                boolean isKitModificationMajor = updateQuestion(
                    question,
                    dslQuestion,
                    savedKit.getActiveVersionId(),
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
            postUpdateMeasures,
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
                                              Map<String, Long> postUpdateMeasures,
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

            createQuestions(newDslQuestionsMap,
                postUpdateQuestionnaires,
                postUpdateMeasures,
                postUpdateAttributes,
                postUpdateMaturityLevels,
                kitVersionId,
                currentUserId);

            if (!newDslQuestionsMap.isEmpty())
                questionAddition = true;
        }
        return questionAddition;
    }

    private void createQuestions(Map<String, QuestionDslModel> dslQuestions,
                                 Map<String, Long> questionnaires,
                                 Map<String, Long> measures,
                                 Map<String, Long> attributes,
                                 Map<String, Long> maturityLevels,
                                 long kitVersionId,
                                 UUID currentUserId) {
        if (dslQuestions == null || dslQuestions.isEmpty())
            return;

        dslQuestions.values().forEach(dslQuestion -> {
            Long questionnaireId = questionnaires.get(dslQuestion.getQuestionnaireCode());
            Long measureId = measures.get(dslQuestion.getQuestionnaireCode());
            var createAnswerRangeParam = new CreateAnswerRangePort.Param(kitVersionId, null, null, false, currentUserId);
            long answerRangeId = createAnswerRangePort.persist(createAnswerRangeParam);

            var createParam = toCreateQuestionParam(kitVersionId, questionnaireId, measureId, answerRangeId, currentUserId, dslQuestion);
            Long questionId = createQuestionPort.persist(createParam);
            log.debug("Question[id={}, code={}, questionnaireCode={}] created.",
                questionId, dslQuestion.getCode(), questionnaires.get(dslQuestion.getQuestionnaireCode()));

            dslQuestion.getAnswerOptions().forEach(option ->
                createAnswerOption(option,
                    questionId,
                    answerRangeId,
                    kitVersionId,
                    currentUserId));

            dslQuestion.getQuestionImpacts().forEach(impact ->
                createImpact(impact, kitVersionId, questionId, attributes, maturityLevels, currentUserId));
        });
    }

    private CreateQuestionPort.Param toCreateQuestionParam(Long kitVersionId,
                                                           Long questionnaireId,
                                                           Long measureId,
                                                           Long answerRangeId,
                                                           UUID currentUserId,
                                                           QuestionDslModel dslQuestion) {
        return new CreateQuestionPort.Param(
            dslQuestion.getCode(),
            dslQuestion.getTitle(),
            dslQuestion.getIndex(),
            dslQuestion.getDescription(),
            dslQuestion.isMayNotBeApplicable(),
            dslQuestion.isAdvisable(),
            kitVersionId,
            questionnaireId,
            measureId,
            answerRangeId,
            currentUserId);
    }

    private void createAnswerOption(AnswerOptionDslModel option,
                                    Long questionId,
                                    long answerRangeId,
                                    long kitVersionId,
                                    UUID currentUserId) {
        var createOptionParam = new CreateAnswerOptionPort.Param(
            option.getCaption(),
            option.getIndex(),
            answerRangeId,
            option.getValue(),
            kitVersionId,
            currentUserId);
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
    }


    private boolean updateQuestion(Question savedQuestion,
                                   QuestionDslModel dslQuestion,
                                   Long kitVersionId,
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
                dslQuestion.getCode(),
                dslQuestion.getTitle(),
                dslQuestion.getIndex(),
                dslQuestion.getDescription(),
                dslQuestion.isMayNotBeApplicable(),
                dslQuestion.isAdvisable(),
                savedQuestion.getAnswerRangeId(),
                LocalDateTime.now(),
                currentUserId
            );
            updateQuestionPort.update(updateParam);
            log.debug("Question[id={}] updated.", savedQuestion.getId());
            if (!savedQuestion.getMayNotBeApplicable().equals(dslQuestion.isMayNotBeApplicable())) {
                isMajorUpdate = true;
            }
        }

        if (dslQuestion.getAnswerOptions() != null)
            updateAnswerOptions(savedQuestion, dslQuestion, kitVersionId, currentUserId);
        boolean isMajorUpdateQuestionImpact = updateQuestionImpacts(savedQuestion,
            dslQuestion,
            kitVersionId,
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
                updateAnswerOptionPort.updateTitle(new UpdateAnswerOptionPort.UpdateTitleParam(
                    optionEntry.getValue().getId(),
                    kitVersionId,
                    dslOptionTitle,
                    LocalDateTime.now(),
                    currentUserId));
                log.debug("AnswerOption[id={}, index={}, newTitle{}, questionId{}] updated.",
                    optionEntry.getValue().getId(), optionEntry.getKey(), dslOptionTitle, savedQuestion.getId());
            }
        }
    }

    private record AttributeLevel(String attributeCode, String levelCode) {
    }

    private boolean updateQuestionImpacts(Question savedQuestion,
                                          QuestionDslModel dslQuestion,
                                          Long kitVersionId,
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
            var updateParam = new UpdateQuestionImpactPort.UpdateWeightParam(
                savedImpact.getId(),
                savedImpact.getKitVersionId(),
                dslImpact.getWeight(),
                savedImpact.getQuestionId(),
                LocalDateTime.now(),
                currentUserId
            );
            updateQuestionImpactPort.updateWeight(updateParam);
            log.debug("QuestionImpact[id={}, questionId={}] updated.", savedImpact.getId(), savedQuestion.getId());
            isMajorUpdate = true;
        }

        return isMajorUpdate;
    }
}
