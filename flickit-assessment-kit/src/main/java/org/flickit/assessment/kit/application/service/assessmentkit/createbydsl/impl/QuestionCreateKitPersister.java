package org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.QuestionImpact;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionImpactDslModel;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answerrange.CreateAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.question.CreateQuestionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.CreateQuestionImpactPort;
import org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext.KEY_ANSWER_RANGES;
import static org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionCreateKitPersister implements CreateKitPersister {

    private final CreateQuestionPort createQuestionPort;
    private final CreateQuestionImpactPort createQuestionImpactPort;
    private final CreateAnswerOptionPort createAnswerOptionPort;
    private final CreateAnswerRangePort createAnswerRangePort;

    @Override
    public int order() {
        return 6;
    }

    @Override
    public void persist(CreateKitPersisterContext ctx, AssessmentKitDslModel dslKit, Long kitVersionId, UUID currentUserId) {
        Map<String, Long> questionnaires = ctx.get(KEY_QUESTIONNAIRES);
        Map<String, Long> attributes = ctx.get(KEY_ATTRIBUTES);
        Map<String, Long> maturityLevels = ctx.get(KEY_MATURITY_LEVELS);
        Map<String, Long> answerRanges = ctx.get(KEY_ANSWER_RANGES);

        Map<String, Map<String, QuestionDslModel>> dslQuestionnaireToQuestionsMap = dslKit.getQuestions().stream()
            .collect(groupingBy(QuestionDslModel::getQuestionnaireCode, toMap(QuestionDslModel::getCode, model -> model)));

        questionnaires.keySet().forEach(code ->
            createQuestions(
                dslQuestionnaireToQuestionsMap.get(code),
                questionnaires.get(code),
                attributes,
                maturityLevels,
                answerRanges,
                kitVersionId,
                currentUserId
            ));

    }

    private void createQuestions(Map<String, QuestionDslModel> dslQuestions,
                                 Long questionnaireId,
                                 Map<String, Long> attributes,
                                 Map<String, Long> maturityLevels,
                                 Map<String, Long> answerRanges,
                                 Long kitVersionId,
                                 UUID currentUserId) {

        if (dslQuestions == null || dslQuestions.isEmpty())
            return;

        dslQuestions.values().forEach(dslQuestion ->
            createQuestion(dslQuestion, questionnaireId, attributes, maturityLevels, answerRanges, kitVersionId, currentUserId)
        );
    }

    private void createQuestion(QuestionDslModel dslQuestion,
                                Long questionnaireId,
                                Map<String, Long> attributes,
                                Map<String, Long> maturityLevels,
                                Map<String, Long> answerRanges,
                                Long kitVersionId,
                                UUID currentUserId) {
        var param = new CreateAnswerRangePort.Param(kitVersionId, null, null, false, currentUserId);
        long answerRangeId;
        if (dslQuestion.getAnswerRangeCode() == null) {
            answerRangeId = createAnswerRangePort.persist(param);
            createAnswerOptions(dslQuestion, answerRangeId, kitVersionId, currentUserId);
        } else
            answerRangeId = answerRanges.get(dslQuestion.getAnswerRangeCode());

        var createParam = new CreateQuestionPort.Param(
            dslQuestion.getCode(),
            dslQuestion.getTitle(),
            dslQuestion.getIndex(),
            dslQuestion.getDescription(),
            dslQuestion.isMayNotBeApplicable(),
            dslQuestion.isAdvisable(),
            kitVersionId,
            questionnaireId,
            answerRangeId,
            currentUserId
        );

        Long questionId = createQuestionPort.persist(createParam);
        log.debug("Question[id={}, code={}, questionnaireCode={}] created.",
            questionId, dslQuestion.getCode(), dslQuestion.getQuestionnaireCode());

        dslQuestion.getQuestionImpacts().forEach(impact -> {
            Long attributeId = attributes.get(impact.getAttributeCode());
            Long maturityLevelId = maturityLevels.get(impact.getMaturityLevel().getCode());
            createImpact(impact, kitVersionId, questionId, attributeId, maturityLevelId, currentUserId);
        });
    }

    private void createAnswerOptions(QuestionDslModel dslQuestion, Long answerRangeId, Long kitVersionId, UUID currentUserId) {
        dslQuestion.getAnswerOptions().forEach(option -> {
            var createOptionParam = new CreateAnswerOptionPort.Param(option.getCaption(),
                option.getIndex(),
                answerRangeId,
                option.getValue(),
                kitVersionId,
                currentUserId);

            var optionId = createAnswerOptionPort.persist(createOptionParam);
            log.debug("AnswerOption[Id={}, index={}, title={}, answerRangeId={}] created.",
                optionId, option.getIndex(), option.getCaption(), answerRangeId);
        });
    }

    private void createImpact(QuestionImpactDslModel dslQuestionImpact,
                              Long kitVersionId,
                              Long questionId,
                              Long attributeId,
                              Long maturityLevelId,
                              UUID currentUserId) {

        QuestionImpact newQuestionImpact = new QuestionImpact(
            null,
            attributeId,
            maturityLevelId,
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
}
