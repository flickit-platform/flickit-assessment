package org.flickit.assessment.kit.application.service.assessmentkit.create.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.domain.dsl.*;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionsByQuestionPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.CreateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.question.CreateQuestionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.CreateQuestionImpactPort;
import org.flickit.assessment.kit.application.service.assessmentkit.create.CreateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.create.CreateKitPersisterContext;
import org.springframework.stereotype.Service;

import java.util.Map;

import static java.util.stream.Collectors.*;
import static org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionCreateKitPersister implements CreateKitPersister {

    private final CreateQuestionPort createQuestionPort;
    private final CreateQuestionImpactPort createQuestionImpactPort;
    private final CreateAnswerOptionImpactPort createAnswerOptionImpactPort;
    private final CreateAnswerOptionPort createAnswerOptionPort;
    private final LoadAnswerOptionsByQuestionPort loadAnswerOptionsByQuestionPort;

    @Override
    public int order() {
        return 5;
    }

    @Override
    public void persist(CreateKitPersisterContext ctx, AssessmentKitDslModel dslKit, Long kitId) {
        Map<String, Questionnaire> questionnaires = ctx.get(KEY_QUESTIONNAIRES);
        Map<String, Attribute> attributes = ctx.get(KEY_ATTRIBUTES);
        Map<String, MaturityLevel> maturityLevels = ctx.get(KEY_MATURITY_LEVELS);

        Map<String, Map<String, QuestionDslModel>> dslQuestionnaireToQuestionsMap = dslKit.getQuestions().stream()
            .collect(groupingBy(QuestionDslModel::getQuestionnaireCode,
                toMap(QuestionDslModel::getCode, model -> model)
            ));

        questionnaires.keySet().forEach(code -> createQuestions(
            dslQuestionnaireToQuestionsMap.get(code),
            questionnaires,
            attributes,
            maturityLevels
        ));

    }

    private void createQuestions(Map<String, QuestionDslModel> dslQuestions,
                                 Map<String, Questionnaire> questionnaires,
                                 Map<String, Attribute> attributes,
                                 Map<String, MaturityLevel> maturityLevels) {
        if (dslQuestions == null || dslQuestions.isEmpty())
            return;

        dslQuestions.values().forEach(dslQuestion -> {
            var createParam = new CreateQuestionPort.Param(
                dslQuestion.getCode(),
                dslQuestion.getTitle(),
                dslQuestion.getDescription(),
                dslQuestion.getIndex(),
                questionnaires.get(dslQuestion.getQuestionnaireCode()).getId(),
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

    private void createImpact(QuestionImpactDslModel dslQuestionImpact,
                              Long questionId,
                              Map<String, Attribute> attributes,
                              Map<String, MaturityLevel> maturityLevels) {
        QuestionImpact newQuestionImpact = new QuestionImpact(
            null,
            attributes.get(dslQuestionImpact.getAttributeCode()).getId(),
            maturityLevels.get(dslQuestionImpact.getMaturityLevel().getTitle()).getId(),
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
}
