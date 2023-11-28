package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionImpactDslModel;
import org.flickit.assessment.kit.application.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionByIndexPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.CreateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.DeleteAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.UpdateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelByCodePort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.qualityattribute.LoadQualityAttributeByCodePort;
import org.flickit.assessment.kit.application.port.out.qualityattribute.LoadQualityAttributePort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionByCodePort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.CreateQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.DeleteQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.LoadQuestionImpactByAttributeAndMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.UpdateQuestionImpactPort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_KIT_BY_DSL_ATTRIBUTE_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_KIT_BY_DSL_MATURITY_LEVEL_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionUpdateKitPersister implements UpdateKitPersister {

    private final UpdateQuestionPort updateQuestionPort;
    private final LoadMaturityLevelPort loadMaturityLevelPort;
    private final LoadQualityAttributePort loadQualityAttributePort;
    private final LoadMaturityLevelByCodePort loadMaturityLevelByCodePort;
    private final LoadQualityAttributeByCodePort loadQualityAttributeByCodePort;
    private final LoadQuestionByCodePort loadQuestionByCodePort;
    private final CreateQuestionImpactPort createQuestionImpactPort;
    private final DeleteQuestionImpactPort deleteQuestionImpactPort;
    private final UpdateQuestionImpactPort updateQuestionImpactPort;
    private final CreateAnswerOptionImpactPort createAnswerOptionImpactPort;
    private final LoadAnswerOptionByIndexPort loadAnswerOptionByIndexPort;
    private final DeleteAnswerOptionImpactPort deleteAnswerOptionImpactPort;
    private final UpdateAnswerOptionImpactPort updateAnswerOptionImpactPort;
    private final LoadQuestionImpactByAttributeAndMaturityLevelPort loadQuestionImpactByAttributeAndMaturityLevelPort;

    @Override
    public UpdateKitPersisterResult persist(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        var savedQuestions = savedKit.getQuestionnaires().stream().flatMap(q -> q.getQuestions().stream()).toList();
        var dslQuestions = dslKit.getQuestions();

        Map<String, Question> savedQuestionCodesMap = savedQuestions.stream().collect(Collectors.toMap(Question::getCode, i -> i));
        Map<String, QuestionDslModel> dslQuestionCodesMap = dslQuestions.stream().collect(Collectors.toMap(QuestionDslModel::getCode, i -> i));

        List<String> sameLevels = sameCodesInNewDsl(savedQuestionCodesMap.keySet(), dslQuestionCodesMap.keySet());

        boolean invalidateResults = false;
        for (String i : sameLevels) {
            boolean invalidOnUpdate = updateQuestion(savedQuestionCodesMap.get(i), dslQuestionCodesMap.get(i), savedKit.getId());
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
                dslQuestion.isMayNotBeApplicable()
            );
            updateQuestionPort.update(updateParam);
            log.debug("A question with code [{}] is updated.", dslQuestion.getCode());
            if (savedQuestion.getMayNotBeApplicable() != dslQuestion.isMayNotBeApplicable()) {
                invalidateResults = true;
            }
        }

        if (savedQuestion.getImpacts() != null || dslQuestion.getQuestionImpacts() != null) {
            invalidateResults = invalidateResults || updateQuestionImpacts(savedQuestion, dslQuestion, kitId);
        }

        return invalidateResults;
    }

    private boolean updateQuestionImpacts(Question savedQuestion, QuestionDslModel dslQuestion, long kitId) {
        boolean invalidateResults = false;
        Map<QuestionImpact.Code, QuestionImpact> savedImpactCodesMap = savedQuestion.getImpacts().stream()
            .collect(Collectors.toMap(this::createQuestionImpactCode, i -> i));
        Map<QuestionImpact.Code, QuestionImpactDslModel> dslImpactCodesMap = dslQuestion.getQuestionImpacts().stream()
            .collect(Collectors.toMap(i -> new QuestionImpact.Code(i.getAttributeCode(), i.getMaturityLevel().getCode()), i -> i));

        List<QuestionImpact.Code> newImpacts = newImpactsInNewDsl(savedImpactCodesMap.keySet(), dslImpactCodesMap.keySet());
        List<QuestionImpact.Code> deletedImpacts = deletedImpactsInNewDsl(savedImpactCodesMap.keySet(), dslImpactCodesMap.keySet());
        List<QuestionImpact.Code> sameImpacts = sameImpactsInNewDsl(savedImpactCodesMap.keySet(), dslImpactCodesMap.keySet());

        newImpacts.forEach(i -> createImpact(dslImpactCodesMap.get(i), dslImpactCodesMap.get(i).getQuestion().getCode(), kitId));
        deletedImpacts.forEach(i -> deleteImpact(savedImpactCodesMap.get(new QuestionImpact.Code(i.attributeCode(), i.maturityLevelCode()))));
        for (QuestionImpact.Code i : sameImpacts) {
            invalidateResults = updateImpact(savedImpactCodesMap.get(i), dslImpactCodesMap.get(i), kitId);
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
        );
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

    private void createImpact(QuestionImpactDslModel dslQuestionImpact, String questionCode, Long kitId) {
        long questionId = loadQuestionByCodePort.loadByCode(questionCode).getId();
        QuestionImpact newQuestionImpact = new QuestionImpact(
            null,
            loadQualityAttributeByCodePort.loadByCode(dslQuestionImpact.getAttributeCode()).getId(),
            loadMaturityLevelByCodePort.loadByCode(dslQuestionImpact.getMaturityLevel().getCode(), kitId).getId(),
            dslQuestionImpact.getWeight(),
            questionId
        );
        Long impactId = createQuestionImpactPort.persist(newQuestionImpact);
        log.debug("Question impact with attribute code [{}] and maturity level code [{}] is created.",
            dslQuestionImpact.getAttributeCode(), dslQuestionImpact.getMaturityLevel().getCode());

        var answerOptionImpactCreateParamList = dslQuestionImpact.getOptionsIndextoValueMap().keySet().stream()
            .map(key -> new CreateAnswerOptionImpactPort.Param(
                impactId,
                loadAnswerOptionByIndexPort.loadByIndex(key, questionId).getId(),
                dslQuestionImpact.getOptionsIndextoValueMap().get(key)))
            .toList();
        answerOptionImpactCreateParamList.forEach(a -> {
            Long optionImpactId = createAnswerOptionImpactPort.persist(a);
            log.debug("Answer option impact with id [{}] is created.", optionImpactId);
        });
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

    private boolean updateImpact(QuestionImpact savedImpact, QuestionImpactDslModel dslImpact, Long kitId) {
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
            invalidateResult = invalidateResult || updateOptionImpacts(savedImpact, dslImpact, kitId);
        }

        return invalidateResult;
    }

    private boolean updateOptionImpacts(QuestionImpact savedImpact, QuestionImpactDslModel dslImpact, Long kitId) {
        boolean invalidateResults = false;
        Map<AnswerOptionImpact.Code, AnswerOptionImpact> savedOptionImpactCodesMap = savedImpact.getOptionImpacts().stream()
            .collect(Collectors.toMap(a -> new AnswerOptionImpact.Code(savedImpact.getId(), a.getOptionId()), a -> a));
        Map<AnswerOptionImpact.Code, AnswerOptionImpact> dslOptionImpactCodesMap = dslImpact.getOptionsIndextoValueMap().keySet().stream()
            .collect(Collectors.toMap(
                k -> buildOptionImpactCode(new QuestionImpact.Code(dslImpact.getAttributeCode(), dslImpact.getMaturityLevel().getCode()), k, dslImpact.getQuestion().getCode(), kitId),
                k -> buildOptionImpact(k, dslImpact.getOptionsIndextoValueMap().get(k), dslImpact.getQuestion().getCode())));

        List<AnswerOptionImpact.Code> newOptionImpacts = newOptionImpactsInNewDsl(savedOptionImpactCodesMap.keySet(), dslOptionImpactCodesMap.keySet());
        List<AnswerOptionImpact.Code> deletedOptionImpacts = deletedOptionImpactsInNewDsl(savedOptionImpactCodesMap.keySet(), dslOptionImpactCodesMap.keySet());
        List<AnswerOptionImpact.Code> sameOptionImpacts = sameOptionImpactsInNewDsl(savedOptionImpactCodesMap.keySet(), dslOptionImpactCodesMap.keySet());

        newOptionImpacts.forEach(i -> createAnswerOptionImpact(i, dslOptionImpactCodesMap.get(i).getValue()));
        deletedOptionImpacts.forEach(i -> deleteAnswerOptionImpactPort.delete(i.impactId(), i.optionId()));
        for (AnswerOptionImpact.Code i : sameOptionImpacts) {
            invalidateResults = updateAnswerOptionImpact(savedOptionImpactCodesMap.get(i), dslOptionImpactCodesMap.get(i), i);
        }

        if (invalidateResults || !newOptionImpacts.isEmpty() || !deletedOptionImpacts.isEmpty())
            invalidateResults = true;

        return invalidateResults;
    }

    private AnswerOptionImpact.Code buildOptionImpactCode(QuestionImpact.Code impactCode, Integer index, String questionCode, Long kitId) {
        return new AnswerOptionImpact.Code(
            loadQuestionImpactByAttributeAndMaturityLevelPort.loadByAttributeCodeAndMaturityLevelCode(
                    loadQualityAttributeByCodePort.loadByCode(impactCode.attributeCode()).getId(),
                    loadMaturityLevelByCodePort.loadByCode(impactCode.maturityLevelCode(), kitId).getId())
                .getId(),
            loadAnswerOptionByIndexPort.loadByIndex(index, loadQuestionByCodePort.loadByCode(questionCode).getId()).getId()
        );
    }

    private AnswerOptionImpact buildOptionImpact(Integer index, Double value, String questionCode) {
        return new AnswerOptionImpact(
            loadAnswerOptionByIndexPort.loadByIndex(index, loadQuestionByCodePort.loadByCode(questionCode).getId()).getId(),
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

    private void createAnswerOptionImpact(AnswerOptionImpact.Code code, Double value) {
        var createParam = new CreateAnswerOptionImpactPort.Param(
            code.impactId(),
            code.optionId(),
            value
        );
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
            invalidateResults = true;
        }

        return invalidateResults;
    }

}
