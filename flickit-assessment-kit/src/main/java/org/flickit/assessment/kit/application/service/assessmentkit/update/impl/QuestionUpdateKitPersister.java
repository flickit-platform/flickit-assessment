package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.domain.dsl.*;
import org.flickit.assessment.kit.application.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionByIndexPort;
import org.flickit.assessment.kit.application.port.out.answeroption.UpdateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.CreateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.DeleteAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.UpdateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelByCodePort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.qualityattribute.LoadQualityAttributeByCodePort;
import org.flickit.assessment.kit.application.port.out.qualityattribute.LoadQualityAttributePort;
import org.flickit.assessment.kit.application.port.out.question.CreateQuestionPort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.CreateQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.DeleteQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.UpdateQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnaireByCodePort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionUpdateKitPersister implements UpdateKitPersister {

    private final UpdateQuestionPort updateQuestionPort;
    private final CreateQuestionPort createQuestionPort;
    private final LoadQuestionnaireByCodePort loadQuestionnaireByCodePort;
    private final LoadMaturityLevelPort loadMaturityLevelPort;
    private final LoadQualityAttributePort loadQualityAttributePort;
    private final LoadMaturityLevelByCodePort loadMaturityLevelByCodePort;
    private final LoadQualityAttributeByCodePort loadQualityAttributeByCodePort;
    private final CreateQuestionImpactPort createQuestionImpactPort;
    private final DeleteQuestionImpactPort deleteQuestionImpactPort;
    private final UpdateQuestionImpactPort updateQuestionImpactPort;
    private final CreateAnswerOptionImpactPort createAnswerOptionImpactPort;
    private final DeleteAnswerOptionImpactPort deleteAnswerOptionImpactPort;
    private final UpdateAnswerOptionImpactPort updateAnswerOptionImpactPort;
    private final UpdateAnswerOptionPort updateAnswerOptionPort;
    private final LoadAnswerOptionByIndexPort loadAnswerOptionByIndexPort;
    private final CreateAnswerOptionPort createAnswerOptionPort;

    @Override
    public UpdateKitPersisterResult persist(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        var savedQuestionnaires = savedKit.getQuestionnaires();
        var dslQuestionnaires = dslKit.getQuestionnaires();

        Map<String, Questionnaire> savedQuestionnaireCodesMap = savedQuestionnaires.stream().collect(toMap(Questionnaire::getCode, q -> q));
        Map<String, QuestionnaireDslModel> dslQuestionnaireCodesMap = dslQuestionnaires.stream().collect(toMap(QuestionnaireDslModel::getCode, q -> q));

        List<String> newQuestionnaires = dslQuestionnaireCodesMap.keySet().stream()
            .filter(s -> savedQuestionnaireCodesMap.keySet().stream()
                .noneMatch(s::equals))
            .toList();

        // Assuming that new questionnaires have been created in Questionnaire persister
        createQuestion(savedKit.getId(), dslKit.getQuestions(), newQuestionnaires);

        List<String> sameQuestionnaires = savedQuestionnaireCodesMap.keySet().stream()
            .filter(s -> dslQuestionnaireCodesMap.keySet().stream()
                .anyMatch(s::equals))
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
                    boolean invalidOnUpdate = updateQuestion(savedQuestionCodesMap.get(i), dslQuestionCodesMap.get(i), savedKit.getId());
                    if (invalidOnUpdate)
                        invalidateResults = true;
                }
            }
        }

        return new UpdateKitPersisterResult(invalidateResults || !newQuestionnaires.isEmpty());
    }

    private void createQuestion(Long kitId, List<QuestionDslModel> dslQuestions, List<String> newQuestionnaires) {
        newQuestionnaires.forEach(q -> {
            var newQuestions = dslQuestions.stream().filter(i -> i.getQuestionnaireCode().equals(q)).toList();
            if (Objects.nonNull(newQuestions) && !newQuestions.isEmpty()) {
                var questionnaire = loadQuestionnaireByCodePort.loadByCode(q, kitId);
                newQuestions.forEach(i -> {
                    var createParam = new CreateQuestionPort.Param(i.getCode(), i.getTitle(), i.getDescription(), i.getIndex(), questionnaire.getId(), i.isMayNotBeApplicable());
                    Long questionId = createQuestionPort.persist(createParam);
                    log.debug("Question with id [{}] is created.", questionId);

                    var newOptions = i.getAnswerOptions();
                    if (Objects.nonNull(newOptions) && !newOptions.isEmpty()) {
                        newOptions.forEach(n -> createAnswerOption(n, questionId));
                    }

                    var newImpacts = i.getQuestionImpacts();
                    if (Objects.nonNull(newImpacts) && !newImpacts.isEmpty()) {
                        newImpacts.forEach(n -> createImpact(questionId, n, kitId));
                    }
                });
            }
        });
    }

    private void createAnswerOption(AnswerOptionDslModel n, Long questionId) {
        var createOptionParam = new CreateAnswerOptionPort.Param(n.getCaption(), questionId, n.getIndex());
        var optionId = createAnswerOptionPort.persist(createOptionParam);
        log.debug("Answer option with id [{}] is created.", optionId);
    }

    @Override
    public int order() {
        return 5;
    }

    private List<String> sameCodesInNewDsl(Set<String> savedItemCodes, Set<String> newItemCodes) {
        return savedItemCodes.stream()
            .filter(s -> newItemCodes.stream()
                .anyMatch(i -> i.equals(s)))
            .toList();
    }

    private boolean updateQuestion(Question savedQuestion, QuestionDslModel dslQuestion, long kitId) {
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
            log.debug("A question with id [{}] is updated.", savedQuestion.getId());
            if (savedQuestion.getMayNotBeApplicable() != dslQuestion.isMayNotBeApplicable()) {
                invalidateResults = true;
            }
        }

        if (savedQuestion.getOptions() != null || dslQuestion.getAnswerOptions() != null) {
            invalidateResults = invalidateResults || updateAnswerOptions(savedQuestion, dslQuestion);
        }

        if (savedQuestion.getImpacts() != null || dslQuestion.getQuestionImpacts() != null) {
            invalidateResults = invalidateResults || updateQuestionImpacts(savedQuestion, dslQuestion, kitId);
        }

        return invalidateResults;
    }

    private boolean updateAnswerOptions(Question savedQuestion, QuestionDslModel dslQuestion) {
        boolean invalidateResults = false;
        Map<AnswerOption.Code, AnswerOption> savedOptionCodesMap = savedQuestion.getOptions().stream()
            .collect(toMap(a -> new AnswerOption.Code(savedQuestion.getId(), a.getIndex()), a -> a));
        Map<AnswerOption.Code, AnswerOptionDslModel> dslOptionCodesMap = dslQuestion.getAnswerOptions().stream()
            .collect(toMap(a -> new AnswerOption.Code(savedQuestion.getId(), a.getIndex()), a -> a));

        List<AnswerOption.Code> sameOptions = sameOptionsInNewDsl(savedOptionCodesMap.keySet(), dslOptionCodesMap.keySet());

        for (AnswerOption.Code code : sameOptions) {
            invalidateResults = invalidateResults || updateOptions(savedOptionCodesMap.get(code), dslOptionCodesMap.get(code));
        }

        return invalidateResults;
    }

    private List<AnswerOption.Code> sameOptionsInNewDsl(Set<AnswerOption.Code> savedOptionCodes, Set<AnswerOption.Code> dslOptionCodes) {
        return savedOptionCodes.stream()
            .filter(i -> dslOptionCodes.stream()
                .anyMatch(s -> s.questionId() == i.questionId() && s.index() == i.index()))
            .toList();
    }

    private boolean updateOptions(AnswerOption savedOption, AnswerOptionDslModel dslOption) {
        boolean invalidateResults = false;
        if (!Objects.equals(savedOption.getTitle(), dslOption.getCaption())) {
            updateAnswerOptionPort.update(new UpdateAnswerOptionPort.Param(savedOption.getId(), dslOption.getCaption()));
            log.debug("Answer option with id [{}] is updated.", savedOption.getId());
            invalidateResults = true;
        }
        return invalidateResults;
    }

    private boolean updateQuestionImpacts(Question savedQuestion, QuestionDslModel dslQuestion, long kitId) {
        boolean invalidateResults = false;
        Map<QuestionImpact.Code, QuestionImpact> savedImpactCodesMap = savedQuestion.getImpacts().stream()
            .collect(toMap(this::createQuestionImpactCode, i -> i));
        Map<QuestionImpact.Code, QuestionImpactDslModel> dslImpactCodesMap = dslQuestion.getQuestionImpacts().stream()
            .collect(toMap(i -> new QuestionImpact.Code(i.getAttributeCode(), i.getMaturityLevel().getTitle()), i -> i));

        List<QuestionImpact.Code> newImpacts = newImpactsInNewDsl(savedImpactCodesMap.keySet(), dslImpactCodesMap.keySet());
        List<QuestionImpact.Code> deletedImpacts = deletedImpactsInNewDsl(savedImpactCodesMap.keySet(), dslImpactCodesMap.keySet());
        List<QuestionImpact.Code> sameImpacts = sameImpactsInNewDsl(savedImpactCodesMap.keySet(), dslImpactCodesMap.keySet());

        newImpacts.forEach(i -> createImpact(savedQuestion.getId(), dslImpactCodesMap.get(i), kitId));
        deletedImpacts.forEach(i -> deleteImpact(savedImpactCodesMap.get(new QuestionImpact.Code(i.attributeCode(), i.maturityLevelCode()))));
        for (QuestionImpact.Code i : sameImpacts) {
            invalidateResults = invalidateResults || updateImpact(savedQuestion, savedImpactCodesMap.get(i), dslImpactCodesMap.get(i));
        }

        if (invalidateResults || !newImpacts.isEmpty() || !deletedImpacts.isEmpty())
            invalidateResults = true;

        return invalidateResults;
    }

    private QuestionImpact.Code createQuestionImpactCode(QuestionImpact impact) {
        Attribute attribute = loadQualityAttributePort.load(impact.getAttributeId()).orElseThrow(
            () -> new ResourceNotFoundException(UPDATE_KIT_BY_DSL_ATTRIBUTE_NOT_FOUND)
        );
        MaturityLevel maturityLevel = loadMaturityLevelPort.load(impact.getMaturityLevelId()).orElseThrow(
            () -> new ResourceNotFoundException(UPDATE_KIT_BY_DSL_MATURITY_LEVEL_NOT_FOUND)
        ); // TODO: we can use loaded maturity levels from kit (but we do not pass kit to this class)
        return new QuestionImpact.Code(attribute.getCode(), maturityLevel.getCode());
    }

    private List<QuestionImpact.Code> newImpactsInNewDsl(Set<QuestionImpact.Code> savedImpactCodes, Set<QuestionImpact.Code> dslImpactCodes) {
        return dslImpactCodes.stream()
            .filter(i -> savedImpactCodes.stream()
                .noneMatch(s -> s.attributeCode().equals(i.attributeCode()) && s.maturityLevelCode().equals(i.maturityLevelCode())))
            .toList();
    }

    private List<QuestionImpact.Code> deletedImpactsInNewDsl(Set<QuestionImpact.Code> savedImpactCodes, Set<QuestionImpact.Code> dslImpactCodes) {
        return savedImpactCodes.stream()
            .filter(i -> dslImpactCodes.stream()
                .noneMatch(s -> s.attributeCode().equals(i.attributeCode()) && s.maturityLevelCode().equals(i.maturityLevelCode())))
            .toList();
    }

    private List<QuestionImpact.Code> sameImpactsInNewDsl(Set<QuestionImpact.Code> savedImpactCodes, Set<QuestionImpact.Code> dslImpactCodes) {
        return savedImpactCodes.stream()
            .filter(i -> dslImpactCodes.stream()
                .anyMatch(s -> s.attributeCode().equals(i.attributeCode()) && s.maturityLevelCode().equals(i.maturityLevelCode())))
            .toList();
    }

    private void createImpact(Long questionId, QuestionImpactDslModel dslQuestionImpact, Long kitId) {
        QuestionImpact newQuestionImpact = new QuestionImpact(
            null,
            loadQualityAttributeByCodePort.loadByCode(dslQuestionImpact.getAttributeCode(), kitId).getId(),
            // TODO: maturity levels can be loaded from kit
            loadMaturityLevelByCodePort.loadByCode(dslQuestionImpact.getMaturityLevel().getTitle(), kitId).getId(),
            dslQuestionImpact.getWeight(),
            questionId
        );
        Long impactId = createQuestionImpactPort.persist(newQuestionImpact);
        log.debug("Question impact with is [{}] is created.", impactId);

        dslQuestionImpact.getOptionsIndextoValueMap().keySet().forEach(index -> createAnswerOptionImpact(
            impactId,
            loadAnswerOptionByIndexPort.loadByIndex(index, questionId).getId(),
            dslQuestionImpact.getOptionsIndextoValueMap().get(index))
        );
    }

    private void deleteImpact(QuestionImpact impact) {
        deleteQuestionImpactPort.delete(impact.getId());
        log.debug("Question impact with id [{}] is deleted.", impact.getId());

        impact.getOptionImpacts().forEach(o -> {
            deleteAnswerOptionImpactPort.delete(impact.getId(), o.getOptionId());
            log.debug("Answer option impact with question impact id [{}], option id [{}] and value [{}] is deleted.",
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
            log.debug("Question impact with id [{}] is updated.", savedImpact.getId());
            invalidateResult = true;
        }

        if (savedImpact.getOptionImpacts() != null || dslImpact.getOptionsIndextoValueMap() != null) {
            invalidateResult = invalidateResult || updateOptionImpacts(savedQuestion, savedImpact, dslImpact);
        }

        return invalidateResult;
    }

    private boolean updateOptionImpacts(Question savedQuestion, QuestionImpact savedImpact, QuestionImpactDslModel dslImpact) {
        boolean invalidateResults = false;
        Map<AnswerOptionImpact.Code, AnswerOptionImpact> savedOptionImpactCodesMap = savedImpact.getOptionImpacts().stream()
            .collect(toMap(a -> new AnswerOptionImpact.Code(savedImpact.getId(), a.getOptionId()), a -> a));
        Map<AnswerOptionImpact.Code, AnswerOptionImpact> dslOptionImpactCodesMap = dslImpact.getOptionsIndextoValueMap().keySet().stream()
            .collect(toMap(k -> buildOptionImpactCode(savedQuestion, savedImpact.getId(), k),
                k -> buildOptionImpact(savedQuestion, k, dslImpact.getOptionsIndextoValueMap().get(k))));

        List<AnswerOptionImpact.Code> newOptionImpacts = newOptionImpactsInNewDsl(savedOptionImpactCodesMap.keySet(), dslOptionImpactCodesMap.keySet());
        List<AnswerOptionImpact.Code> deletedOptionImpacts = deletedOptionImpactsInNewDsl(savedOptionImpactCodesMap.keySet(), dslOptionImpactCodesMap.keySet());
        List<AnswerOptionImpact.Code> sameOptionImpacts = sameOptionImpactsInNewDsl(savedOptionImpactCodesMap.keySet(), dslOptionImpactCodesMap.keySet());

        newOptionImpacts.forEach(i -> createAnswerOptionImpact(i.impactId(), i.optionId(), dslOptionImpactCodesMap.get(i).getValue()));
        deletedOptionImpacts.forEach(i -> {
            deleteAnswerOptionImpactPort.delete(i.impactId(), i.optionId());
            log.debug("Answer option impact with question impact id [{}] and option id [{}] is deleted.", i.impactId(), i.optionId());
        });
        for (AnswerOptionImpact.Code i : sameOptionImpacts) {
            invalidateResults = invalidateResults || updateAnswerOptionImpact(savedOptionImpactCodesMap.get(i), dslOptionImpactCodesMap.get(i), i);
        }

        if (invalidateResults || !newOptionImpacts.isEmpty() || !deletedOptionImpacts.isEmpty())
            invalidateResults = true;

        return invalidateResults;
    }

    private AnswerOptionImpact.Code buildOptionImpactCode(Question savedQuestion, Long impactId, Integer index) {
        return new AnswerOptionImpact.Code(
            impactId,
            savedQuestion.getOptions().stream()
                .filter(o -> o.getQuestionId() == savedQuestion.getId() && o.getIndex() == index)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(UPDATE_KIT_BY_DSL_ANSWER_OPTION_NOT_FOUND))
                .getId()
        );
    }

    private AnswerOptionImpact buildOptionImpact(Question savedQuestion, Integer index, Double value) {
        return new AnswerOptionImpact(
            savedQuestion.getOptions().stream()
                .filter(o -> o.getQuestionId() == savedQuestion.getId() && o.getIndex() == index)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(UPDATE_KIT_BY_DSL_ANSWER_OPTION_NOT_FOUND))
                .getId(),
            value
        );
    }

    private List<AnswerOptionImpact.Code> newOptionImpactsInNewDsl(Set<AnswerOptionImpact.Code> savedOptionImpactCodes, Set<AnswerOptionImpact.Code> dslOptionImpactCodes) {
        return dslOptionImpactCodes.stream()
            .filter(i -> savedOptionImpactCodes.stream()
                .noneMatch(s -> s.optionId().equals(i.optionId()) && s.impactId().equals(i.impactId())))
            .toList();
    }

    private List<AnswerOptionImpact.Code> deletedOptionImpactsInNewDsl(Set<AnswerOptionImpact.Code> savedOptionImpactCodes, Set<AnswerOptionImpact.Code> dslOptionImpactCodes) {
        return savedOptionImpactCodes.stream()
            .filter(i -> dslOptionImpactCodes.stream()
                .noneMatch(s -> s.optionId().equals(i.optionId()) && s.impactId().equals(i.impactId())))
            .toList();
    }

    private List<AnswerOptionImpact.Code> sameOptionImpactsInNewDsl(Set<AnswerOptionImpact.Code> savedOptionImpactCodes, Set<AnswerOptionImpact.Code> dslOptionImpactCodes) {
        return savedOptionImpactCodes.stream()
            .filter(i -> dslOptionImpactCodes.stream()
                .anyMatch(s -> s.optionId().equals(i.optionId()) && s.impactId().equals(i.impactId())))
            .toList();
    }

    private void createAnswerOptionImpact(Long impactId, Long optionId, Double value) {
        var createParam = new CreateAnswerOptionImpactPort.Param(impactId, optionId, value);
        Long optionImpactId = createAnswerOptionImpactPort.persist(createParam);
        log.debug("Answer option impact with id [{}] is created.", optionImpactId);
    }

    private boolean updateAnswerOptionImpact(AnswerOptionImpact savedOptionImpact, AnswerOptionImpact dslOptionImpact, AnswerOptionImpact.Code code) {
        boolean invalidateResults = false;
        if (savedOptionImpact.getValue() != dslOptionImpact.getValue()) {
            var updateParam = new UpdateAnswerOptionImpactPort.Param(
                code.impactId(),
                code.optionId(),
                dslOptionImpact.getValue()
            );
            updateAnswerOptionImpactPort.update(updateParam);
            log.debug("Answer option impact with impact id [{}] and option id [{}] is updated.", code.impactId(), code.optionId());
            invalidateResults = true;
        }

        return invalidateResults;
    }

}
