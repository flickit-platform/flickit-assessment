package org.flickit.assessment.kit.application.service.assessmentkit.create.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionImpactDslModel;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.CreateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.question.CreateQuestionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.CreateQuestionImpactPort;
import org.flickit.assessment.kit.application.service.assessmentkit.create.CreateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.create.CreateKitPersisterContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionCreateKitPersister implements CreateKitPersister {

    private final CreateQuestionPort createQuestionPort;
    private final CreateQuestionImpactPort createQuestionImpactPort;
    private final CreateAnswerOptionImpactPort createAnswerOptionImpactPort;
    private final CreateAnswerOptionPort createAnswerOptionPort;

    @Override
    public int order() {
        return 5;
    }

    @Override
    public void persist(CreateKitPersisterContext ctx, AssessmentKitDslModel dslKit, Long kitId, UUID currentUserId) {
        Map<String, Questionnaire> questionnaires = ctx.get(KEY_QUESTIONNAIRES);
        Map<String, Attribute> attributes = ctx.get(KEY_ATTRIBUTES);
        Map<String, MaturityLevel> maturityLevels = ctx.get(KEY_MATURITY_LEVELS);

        Map<String, Map<String, QuestionDslModel>> dslQuestionnaireToQuestionsMap = dslKit.getQuestions().stream()
            .collect(groupingBy(QuestionDslModel::getQuestionnaireCode, toMap(QuestionDslModel::getCode, model -> model)));

        questionnaires.keySet().forEach(code -> createQuestions(
            dslQuestionnaireToQuestionsMap.get(code),
            questionnaires,
            attributes,
            maturityLevels,
            currentUserId
        ));

    }

    private void createQuestions(Map<String, QuestionDslModel> dslQuestions,
                                 Map<String, Questionnaire> questionnaires,
                                 Map<String, Attribute> attributes,
                                 Map<String, MaturityLevel> maturityLevels,
                                 UUID currentUserId) {

        if (dslQuestions == null || dslQuestions.isEmpty())
            return;

        dslQuestions.values().forEach(dslQuestion -> {
            Long questionnaireId = questionnaires.get(dslQuestion.getQuestionnaireCode()).getId();
            createQuestion(questionnaireId, attributes, maturityLevels, dslQuestion, currentUserId);
        });
    }

    private void createQuestion(Long questionnaireId,
                                Map<String, Attribute> attributes,
                                Map<String, MaturityLevel> maturityLevels,
                                QuestionDslModel dslQuestion,
                                UUID currentUserId) {

        var createParam = new CreateQuestionPort.Param(
            dslQuestion.getCode(),
            dslQuestion.getTitle(),
            dslQuestion.getDescription(),
            dslQuestion.getIndex(),
            questionnaireId,
            dslQuestion.isMayNotBeApplicable(),
            currentUserId);

        Long questionId = createQuestionPort.persist(createParam);
        log.debug("Question[id={}, code={}, questionnaireCode={}] created.",
            questionId, dslQuestion.getCode(), dslQuestion.getQuestionnaireCode());

        Map<Integer, Long> optionIndexToIdMap = createAnswerOptions(dslQuestion, currentUserId, questionId);

        dslQuestion.getQuestionImpacts().forEach(impact -> {
            Long attributeId = attributes.get(impact.getAttributeCode()).getId();
            Long maturityLevelId = maturityLevels.get(impact.getMaturityLevel().getTitle()).getId();
            createImpact(impact, questionId, attributeId, maturityLevelId, optionIndexToIdMap, currentUserId);
        });
    }

    private Map<Integer, Long> createAnswerOptions(QuestionDslModel dslQuestion, UUID currentUserId, Long questionId) {
        Map<Integer, Long> optionIndexToIdMap = new HashMap<>();
        dslQuestion.getAnswerOptions().forEach(option -> {
            var answerOption = createAnswerOption(option, questionId, currentUserId);
            optionIndexToIdMap.put(answerOption.getIndex(), answerOption.getId());
        });
        return optionIndexToIdMap;
    }

    private AnswerOption createAnswerOption(AnswerOptionDslModel option, Long questionId, UUID currentUserId) {
        var createOptionParam = new CreateAnswerOptionPort.Param(option.getCaption(), questionId, option.getIndex(), currentUserId);

        var optionId = createAnswerOptionPort.persist(createOptionParam);
        log.debug("AnswerOption[Id={}, index={}, title={}, questionId={}] created.",
            optionId, option.getIndex(), option.getCaption(), questionId);

        return new AnswerOption(optionId, questionId, option.getCaption(), option.getIndex());
    }

    private void createImpact(QuestionImpactDslModel dslQuestionImpact,
                              Long questionId,
                              Long attributeId,
                              Long maturityLevelId,
                              Map<Integer, Long> optionIndexToIdMap,
                              UUID currentUserId) {

        QuestionImpact newQuestionImpact = new QuestionImpact(
            null,
            attributeId,
            maturityLevelId,
            dslQuestionImpact.getWeight(),
            questionId
        );
        Long impactId = createQuestionImpactPort.persist(newQuestionImpact, currentUserId);
        log.debug("QuestionImpact[impactId={}, questionId={}] created.", impactId, questionId);

        dslQuestionImpact.getOptionsIndextoValueMap().keySet().forEach(
            index -> createAnswerOptionImpact(
                impactId,
                optionIndexToIdMap.get(index),
                dslQuestionImpact.getOptionsIndextoValueMap().get(index),
                currentUserId)
        );
    }

    private void createAnswerOptionImpact(Long questionImpactId, Long optionId, Double value, UUID currentUserId) {
        var createParam = new CreateAnswerOptionImpactPort.Param(questionImpactId, optionId, value, currentUserId);
        Long optionImpactId = createAnswerOptionImpactPort.persist(createParam);
        log.debug("AnswerOptionImpact[id={}, questionImpactId={}, optionId={}] created.", optionImpactId, questionImpactId, optionId);
    }
}
