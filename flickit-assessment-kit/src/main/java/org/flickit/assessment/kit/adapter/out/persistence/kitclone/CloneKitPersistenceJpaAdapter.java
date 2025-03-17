package org.flickit.assessment.kit.adapter.out.persistence.kitclone;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.flickit.assessment.data.jpa.kit.answerrange.AnswerRangeJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.data.jpa.kit.measure.MeasureJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CloneKitPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CloneKitPersistenceJpaAdapter implements CloneKitPort {

    private final QuestionnaireJpaRepository questionnaireRepository;
    private final MeasureJpaRepository measureRepository;
    private final QuestionJpaRepository questionRepository;
    private final MaturityLevelJpaRepository maturityLevelRepository;
    private final SubjectJpaRepository subjectRepository;
    private final AttributeJpaRepository attributeRepository;
    private final AnswerOptionJpaRepository answerOptionRepository;
    private final QuestionImpactJpaRepository questionImpactRepository;
    private final LevelCompetenceJpaRepository levelCompetenceRepository;
    private final AnswerRangeJpaRepository answerRangeRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void cloneKit(Param param) {
        var questionnaireEntities = questionnaireRepository.findAllByKitVersionId(param.activeKitVersionId());
        questionnaireEntities.forEach(entity -> {
            entityManager.detach(entity);
            entity.prepareForClone(param.updatingKitVersionId(), param.clonedBy(), param.cloneTime());
        });

        var measureEntities = measureRepository.findAllByKitVersionId(param.activeKitVersionId());
        measureEntities.forEach(entity -> {
            entityManager.detach(entity);
            entity.prepareForClone(param.updatingKitVersionId(), param.clonedBy(), param.cloneTime());
        });

        var questionEntities = questionRepository.findAllByKitVersionId(param.activeKitVersionId());
        questionEntities.forEach(entity -> {
            entityManager.detach(entity);
            entity.prepareForClone(param.updatingKitVersionId(), param.clonedBy(), param.cloneTime());
        });

        var maturityLevelEntities = maturityLevelRepository.findAllByKitVersionId(param.activeKitVersionId());
        maturityLevelEntities.forEach(entity -> {
            entityManager.detach(entity);
            entity.prepareForClone(param.updatingKitVersionId(), param.clonedBy(), param.cloneTime());
        });

        var subjectEntities = subjectRepository.findAllByKitVersionId(param.activeKitVersionId());
        subjectEntities.forEach(entity -> {
            entityManager.detach(entity);
            entity.prepareForClone(param.updatingKitVersionId(), param.clonedBy(), param.cloneTime());
        });

        var attributeEntities = attributeRepository.findAllByKitVersionId(param.activeKitVersionId());
        attributeEntities.forEach(entity -> {
            entityManager.detach(entity);
            entity.prepareForClone(param.updatingKitVersionId(), param.clonedBy(), param.cloneTime());
        });

        var answerOptionEntities = answerOptionRepository.findAllByKitVersionId(param.activeKitVersionId());
        answerOptionEntities.forEach(entity -> {
            entityManager.detach(entity);
            entity.prepareForClone(param.updatingKitVersionId(), param.clonedBy(), param.cloneTime());
        });

        var questionImpactEntities = questionImpactRepository.findAllByKitVersionId(param.activeKitVersionId());
        questionImpactEntities.forEach(entity -> {
            entityManager.detach(entity);
            entity.prepareForClone(param.updatingKitVersionId(), param.clonedBy(), param.cloneTime());
        });

        var levelCompetenceEntities = levelCompetenceRepository.findAllByKitVersionId(param.activeKitVersionId());
        levelCompetenceEntities.forEach(entity -> {
            entityManager.detach(entity);
            entity.prepareForClone(param.updatingKitVersionId(), param.clonedBy(), param.cloneTime());
        });

        var answerRangeEntities = answerRangeRepository.findAllByKitVersionId(param.activeKitVersionId());
        answerRangeEntities.forEach(entity -> {
            entityManager.detach(entity);
            entity.prepareForClone(param.updatingKitVersionId(), param.clonedBy(), param.cloneTime());
        });

        questionnaireRepository.saveAll(questionnaireEntities);
        measureRepository.saveAll(measureEntities);
        questionRepository.saveAll(questionEntities);
        maturityLevelRepository.saveAll(maturityLevelEntities);
        subjectRepository.saveAll(subjectEntities);
        attributeRepository.saveAll(attributeEntities);
        answerOptionRepository.saveAll(answerOptionEntities);
        questionImpactRepository.saveAll(questionImpactEntities);
        levelCompetenceRepository.saveAll(levelCompetenceEntities);
        answerRangeRepository.saveAll(answerRangeEntities);
    }
}
