package org.flickit.assessment.kit.adapter.out.persistence.kitclone;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
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

import static org.flickit.assessment.kit.common.ErrorMessageKey.QUESTIONNAIRE_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.QUESTION_ID_NOT_FOUND;

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
    private final SubjectQuestionnaireJpaRepository subjectQuestionnaireRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void cloneKit(long activeKitVersionId, long updatingKitVersionId, UUID currentUserId) {

        QuestionnaireJpaEntity questionnaireJpaEntity = questionnaireRepository.findByIdAndKitVersionId(3075L, activeKitVersionId)
                .orElseThrow(() -> new ResourceNotFoundException(QUESTIONNAIRE_ID_NOT_FOUND));

        long id = questionnaireRepository.setNextVal(questionnaireJpaEntity.getId() - 1);
        QuestionnaireJpaEntity clonedQuestionnaireEntity = new QuestionnaireJpaEntity(questionnaireJpaEntity);
        clonedQuestionnaireEntity.setKitVersionId(updatingKitVersionId);
        clonedQuestionnaireEntity.setId(id);
        clonedQuestionnaireEntity.setLastModificationTime(LocalDateTime.now());
        clonedQuestionnaireEntity.setLastModifiedBy(currentUserId);

        entityManager.persist(clonedQuestionnaireEntity);

        QuestionJpaEntity questionJpaEntity = questionRepository.findByIdAndKitVersionId(22831, activeKitVersionId)
                .orElseThrow(() -> new ResourceNotFoundException(QUESTION_ID_NOT_FOUND));

        QuestionJpaEntity clonedQuestionEntity = new QuestionJpaEntity(questionJpaEntity);

        long questionId = questionRepository.setNextVal(questionJpaEntity.getId() - 1);
        clonedQuestionEntity.setKitVersionId(updatingKitVersionId);
        clonedQuestionEntity.setQuestionnaireId(clonedQuestionnaireEntity.getId());
        clonedQuestionEntity.setId(questionId);
        clonedQuestionEntity.setLastModificationTime(LocalDateTime.now());
        clonedQuestionEntity.setLastModifiedBy(currentUserId);

        questionRepository.save(clonedQuestionEntity);



        /*List<QuestionnaireJpaEntity> clonedQuestionnaireEntities =
            questionnaireRepository.findAllByKitVersionId(activeKitVersionId).stream()
                .map(e -> {
                    entityManager.detach(e);
                    QuestionnaireJpaEntity cloned = e.clone();
                    cloned.setKitVersionId(updatingKitVersionId);
                    cloned.setLastModificationTime(LocalDateTime.now());
                    cloned.setLastModifiedBy(currentUserId);
                    return cloned;
                }).toList();

        List<QuestionJpaEntity> clonedQuestionEntities =
            questionRepository.findAllByKitVersionId(activeKitVersionId).stream()
                .map(e -> {
                    entityManager.detach(e);
                    QuestionJpaEntity cloned = e.clone();
                    cloned.setKitVersionId(updatingKitVersionId);
                    cloned.setLastModificationTime(LocalDateTime.now());
                    cloned.setLastModifiedBy(currentUserId);
                    return cloned;
                }).toList();

        List<MaturityLevelJpaEntity> clonedMaturityLevelEntities =
            maturityLevelRepository.findAllByKitVersionId(activeKitVersionId).stream()
                .map(e -> {
                    entityManager.detach(e);
                    MaturityLevelJpaEntity cloned = e.clone();
                    cloned.setKitVersionId(updatingKitVersionId);
                    cloned.setLastModificationTime(LocalDateTime.now());
                    cloned.setLastModifiedBy(currentUserId);
                    return cloned;
                }).toList();

        List<SubjectJpaEntity> clonedSubjectEntities =
            subjectRepository.findAllByKitVersionId(activeKitVersionId).stream()
                .map(e -> {
                    entityManager.detach(e);
                    SubjectJpaEntity cloned = e.clone();
                    cloned.setKitVersionId(updatingKitVersionId);
                    cloned.setLastModificationTime(LocalDateTime.now());
                    cloned.setLastModifiedBy(currentUserId);
                    return cloned;
                }).toList();

        List<AttributeJpaEntity> clonedAttributeEntities =
            attributeRepository.findAllByKitVersionId(activeKitVersionId).stream()
                .map(e -> {
                    entityManager.detach(e);
                    AttributeJpaEntity cloned = e.clone();
                    cloned.setKitVersionId(updatingKitVersionId);
                    cloned.setLastModificationTime(LocalDateTime.now());
                    cloned.setLastModifiedBy(currentUserId);
                    return cloned;
                }).toList();


        List<AnswerOptionJpaEntity> clonedAnswerOptionEntities =
            answerOptionRepository.findAllByKitVersionId(activeKitVersionId).stream()
                .map(e -> {
                    entityManager.detach(e);
                    AnswerOptionJpaEntity cloned = e.clone();
                    cloned.setKitVersionId(updatingKitVersionId);
                    cloned.setLastModificationTime(LocalDateTime.now());
                    cloned.setLastModifiedBy(currentUserId);
                    return cloned;
                }).toList();

        List<AnswerOptionImpactJpaEntity> clonedAnswerOptionImpactEntities =
            answerOptionImpactRepository.findAllByKitVersionId(activeKitVersionId).stream()
                .map(e -> {
                    entityManager.detach(e);
                    AnswerOptionImpactJpaEntity cloned = e.clone();
                    cloned.setKitVersionId(updatingKitVersionId);
                    cloned.setLastModificationTime(LocalDateTime.now());
                    cloned.setLastModifiedBy(currentUserId);
                    return cloned;
                }).toList();

        List<QuestionImpactJpaEntity> clonedQuestionImpactEntities =
            questionImpactRepository.findAllByKitVersionId(activeKitVersionId).stream()
                .map(e -> {
                    entityManager.detach(e);
                    QuestionImpactJpaEntity cloned = e.clone();
                    cloned.setKitVersionId(updatingKitVersionId);
                    cloned.setLastModificationTime(LocalDateTime.now());
                    cloned.setLastModifiedBy(currentUserId);
                    return cloned;
                }).toList();

        List<LevelCompetenceJpaEntity> clonedLevelCompetenceEntities =
            levelCompetenceRepository.findAllByKitVersionId(activeKitVersionId).stream()
                .map(e -> {
                    entityManager.detach(e);
                    LevelCompetenceJpaEntity cloned = e.clone();
                    cloned.setKitVersionId(updatingKitVersionId);
                    cloned.setLastModificationTime(LocalDateTime.now());
                    cloned.setLastModifiedBy(currentUserId);
                    return cloned;
                }).toList();

        List<SubjectQuestionnaireJpaEntity> clonedSubjectQuestionnaireEntities =
            subjectQuestionnaireRepository.findAllByKitVersionId(activeKitVersionId).stream()
                .map(e -> {
                    entityManager.detach(e);
                    SubjectQuestionnaireJpaEntity cloned = e.clone();
                    cloned.setKitVersionId(updatingKitVersionId);
                    return cloned;
                }).toList();

        questionnaireRepository.saveAll(clonedQuestionnaireEntities);
        questionRepository.saveAll(clonedQuestionEntities);
        maturityLevelRepository.saveAll(clonedMaturityLevelEntities);
        subjectRepository.saveAll(clonedSubjectEntities);
        attributeRepository.saveAll(clonedAttributeEntities);
        answerOptionRepository.saveAll(clonedAnswerOptionEntities);
        answerOptionImpactRepository.saveAll(clonedAnswerOptionImpactEntities);
        questionImpactRepository.saveAll(clonedQuestionImpactEntities);
        levelCompetenceRepository.saveAll(clonedLevelCompetenceEntities);
        subjectQuestionnaireRepository.saveAll(clonedSubjectQuestionnaireEntities);*/
    }
}
