package org.flickit.assessment.kit.adapter.out.persistence.kitclone;

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
    private final SubjectQuestionnaireJpaRepository subjectQuestionnaireRepository;

    @Override
    public void cloneKit(long activeKitVersionId, long updatingKitVersionId, UUID currentUserId) {

        List<QuestionnaireJpaEntity> clonedQuestionnaireEntities =
            questionnaireRepository.findAllByKitVersionId(activeKitVersionId).stream()
                .map(e -> {
                    QuestionnaireJpaEntity cloned = e.clone();
                    cloned.setKitVersionId(updatingKitVersionId);
                    cloned.setLastModificationTime(LocalDateTime.now());
                    cloned.setLastModifiedBy(currentUserId);
                    return cloned;
                }).toList();

        List<QuestionJpaEntity> clonedQuestionEntities =
            questionRepository.findAllByKitVersionId(activeKitVersionId).stream()
                .map(e -> {
                    QuestionJpaEntity cloned = e.clone();
                    cloned.setKitVersionId(updatingKitVersionId);
                    cloned.setLastModificationTime(LocalDateTime.now());
                    cloned.setLastModifiedBy(currentUserId);
                    return cloned;
                }).toList();

        List<MaturityLevelJpaEntity> clonedMaturityLevelEntities =
            maturityLevelRepository.findAllByKitVersionId(activeKitVersionId).stream()
                .map(e -> {
                    MaturityLevelJpaEntity cloned = e.clone();
                    cloned.setKitVersionId(updatingKitVersionId);
                    cloned.setLastModificationTime(LocalDateTime.now());
                    cloned.setLastModifiedBy(currentUserId);
                    return cloned;
                }).toList();

        List<SubjectJpaEntity> clonedSubjectEntities =
            subjectRepository.findAllByKitVersionId(activeKitVersionId).stream()
                .map(e -> {
                    SubjectJpaEntity cloned = e.clone();
                    cloned.setKitVersionId(updatingKitVersionId);
                    cloned.setLastModificationTime(LocalDateTime.now());
                    cloned.setLastModifiedBy(currentUserId);
                    return cloned;
                }).toList();

        List<AttributeJpaEntity> clonedAttributeEntities =
            attributeRepository.findAllByKitVersionId(activeKitVersionId).stream()
                .map(e -> {
                    AttributeJpaEntity cloned = e.clone();
                    cloned.setKitVersionId(updatingKitVersionId);
                    cloned.setLastModificationTime(LocalDateTime.now());
                    cloned.setLastModifiedBy(currentUserId);
                    return cloned;
                }).toList();


        List<AnswerOptionJpaEntity> clonedAnswerOptionEntities =
            answerOptionRepository.findAllByKitVersionId(activeKitVersionId).stream()
                .map(e -> {
                    AnswerOptionJpaEntity cloned = e.clone();
                    cloned.setKitVersionId(updatingKitVersionId);
                    cloned.setLastModificationTime(LocalDateTime.now());
                    cloned.setLastModifiedBy(currentUserId);
                    return cloned;
                }).toList();

        List<AnswerOptionImpactJpaEntity> clonedAnswerOptionImpactEntities =
            answerOptionImpactRepository.findAllByKitVersionId(activeKitVersionId).stream()
                .map(e -> {
                    AnswerOptionImpactJpaEntity cloned = e.clone();
                    cloned.setKitVersionId(updatingKitVersionId);
                    cloned.setLastModificationTime(LocalDateTime.now());
                    cloned.setLastModifiedBy(currentUserId);
                    return cloned;
                }).toList();

        List<QuestionImpactJpaEntity> clonedQuestionImpactEntities =
            questionImpactRepository.findAllByKitVersionId(activeKitVersionId).stream()
                .map(e -> {
                    QuestionImpactJpaEntity cloned = e.clone();
                    cloned.setKitVersionId(updatingKitVersionId);
                    cloned.setLastModificationTime(LocalDateTime.now());
                    cloned.setLastModifiedBy(currentUserId);
                    return cloned;
                }).toList();

        List<LevelCompetenceJpaEntity> clonedLevelCompetenceEntities =
            levelCompetenceRepository.findAllByKitVersionId(activeKitVersionId).stream()
                .map(e -> {
                    LevelCompetenceJpaEntity cloned = e.clone();
                    cloned.setKitVersionId(updatingKitVersionId);
                    cloned.setLastModificationTime(LocalDateTime.now());
                    cloned.setLastModifiedBy(currentUserId);
                    return cloned;
                }).toList();

        List<SubjectQuestionnaireJpaEntity> clonedSubjectQuestionnaireEntities =
            subjectQuestionnaireRepository.findAllByKitVersionId(activeKitVersionId).stream()
                .map(e -> {
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
        subjectQuestionnaireRepository.saveAll(clonedSubjectQuestionnaireEntities);
    }
}
