package org.flickit.assessment.kit.adapter.out.persistence.kitclone;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaEntity;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaEntity;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.data.jpa.kit.subjectquestionnaire.SubjectQuestionnaireJpaEntity;
import org.flickit.assessment.data.jpa.kit.subjectquestionnaire.SubjectQuestionnaireJpaRepository;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CloneKitPort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class CloneKitPersistenceJpaAdapter implements CloneKitPort {

    private final QuestionnaireJpaRepository questionnaireRepository;
    private final QuestionJpaRepository questionRepository;
    private final MaturityLevelJpaRepository maturityLevelRepository;
    private final SubjectJpaRepository subjectRepository;
    private final AttributeJpaRepository attributeRepository;
    private final AnswerOptionJpaRepository answerOptionRepository;
    private final AnswerOptionImpactJpaRepository answerOptionImpactRepository;
    private final QuestionImpactJpaRepository questionImpactRepository;
    private final LevelCompetenceJpaRepository levelCompetenceRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void cloneKit(long activeKitVersionId, long updatingKitVersionId, UUID currentUserId) {
        List<QuestionnaireJpaEntity> questionnaireEntities =
            questionnaireRepository.findAllByKitVersionId(activeKitVersionId);

        for (QuestionnaireJpaEntity entity : questionnaireEntities) {
            entityManager.detach(entity);
            entity.setKitVersionId(updatingKitVersionId);
            entity.setLastModificationTime(LocalDateTime.now());
            entity.setLastModifiedBy(currentUserId);
        }

        List<QuestionJpaEntity> questionEntities =
            questionRepository.findAllByKitVersionId(activeKitVersionId);
        for (QuestionJpaEntity entity : questionEntities) {
            entityManager.detach(entity);
            entity.setKitVersionId(updatingKitVersionId);
            entity.setLastModificationTime(LocalDateTime.now());
            entity.setLastModifiedBy(currentUserId);
        }

        List<MaturityLevelJpaEntity> maturityLevelEntities =
            maturityLevelRepository.findAllByKitVersionId(activeKitVersionId);
        for (MaturityLevelJpaEntity entity : maturityLevelEntities) {
            entityManager.detach(entity);
            entity.setKitVersionId(updatingKitVersionId);
            entity.setLastModificationTime(LocalDateTime.now());
            entity.setLastModifiedBy(currentUserId);
        }

        List<SubjectJpaEntity> subjectEntities =
            subjectRepository.findAllByKitVersionId(activeKitVersionId);
        for (SubjectJpaEntity entity : subjectEntities) {
            entityManager.detach(entity);
            entity.setKitVersionId(updatingKitVersionId);
            entity.setLastModificationTime(LocalDateTime.now());
            entity.setLastModifiedBy(currentUserId);
        }

        List<AttributeJpaEntity> attributeEntities =
            attributeRepository.findAllByKitVersionId(activeKitVersionId);
        for (AttributeJpaEntity entity : attributeEntities) {
            entityManager.detach(entity);
            entity.setKitVersionId(updatingKitVersionId);
            entity.setLastModificationTime(LocalDateTime.now());
            entity.setLastModifiedBy(currentUserId);
        }

        List<AnswerOptionJpaEntity> answerOptionEntities =
            answerOptionRepository.findAllByKitVersionId(activeKitVersionId);
        for (AnswerOptionJpaEntity entity : answerOptionEntities) {
            entityManager.detach(entity);
            entity.setKitVersionId(updatingKitVersionId);
            entity.setLastModificationTime(LocalDateTime.now());
            entity.setLastModifiedBy(currentUserId);
        }

        List<AnswerOptionImpactJpaEntity> answerOptionImpactEntities =
            answerOptionImpactRepository.findAllByKitVersionId(activeKitVersionId);
        for (AnswerOptionImpactJpaEntity entity : answerOptionImpactEntities) {
            entityManager.detach(entity);
            entity.setKitVersionId(updatingKitVersionId);
            entity.setLastModificationTime(LocalDateTime.now());
            entity.setLastModifiedBy(currentUserId);
        }

        List<QuestionImpactJpaEntity> questionImpactEntities =
            questionImpactRepository.findAllByKitVersionId(activeKitVersionId);
        for (QuestionImpactJpaEntity entity : questionImpactEntities) {
            entityManager.detach(entity);
            entity.setKitVersionId(updatingKitVersionId);
            entity.setLastModificationTime(LocalDateTime.now());
            entity.setLastModifiedBy(currentUserId);
        }

        List<LevelCompetenceJpaEntity> levelCompetenceEntities =
            levelCompetenceRepository.findAllByKitVersionId(activeKitVersionId);
        for (LevelCompetenceJpaEntity entity : levelCompetenceEntities) {
            entityManager.detach(entity);
            entity.setKitVersionId(updatingKitVersionId);
            entity.setLastModificationTime(LocalDateTime.now());
            entity.setLastModifiedBy(currentUserId);
        }

        questionnaireRepository.saveAll(questionnaireEntities);
        questionRepository.saveAll(questionEntities);
        maturityLevelRepository.saveAll(maturityLevelEntities);
        subjectRepository.saveAll(subjectEntities);
        attributeRepository.saveAll(attributeEntities);
        answerOptionRepository.saveAll(answerOptionEntities);
        questionImpactRepository.saveAll(questionImpactEntities);
        answerOptionImpactRepository.saveAll(answerOptionImpactEntities);
        levelCompetenceRepository.saveAll(levelCompetenceEntities);
    }
}
