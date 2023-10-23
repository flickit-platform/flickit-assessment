package org.flickit.flickitassessmentcore.adapter.out.calculate;

import lombok.NoArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.answer.AnswerJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue.QualityAttributeValueJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.subjectvalue.SubjectValueJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.rest.answeroption.AnswerOptionDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.answeroptionimpact.AnswerOptionImpactDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.levelcompetence.LevelCompetenceDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.maturitylevel.MaturityLevelDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.qualityattribute.QualityAttributeDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.question.QuestionDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.questionimpact.QuestionImpactDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.subject.SubjectDto;
import org.flickit.flickitassessmentcore.application.domain.AssessmentColor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.application.service.constant.AssessmentConstants.NOT_DELETED_DELETION_TIME;

public class AssessmentCalculateInfoCreator {

    private static long kitId = 134L;
    private static int assessmentCounter = 341;
    private static long subjectId = 134L;
    private static long qualityAttributeId = 134L;
    private static long questionImpactId = 134L;
    private static long answerOptionImpactId = 134L;
    private static long levelCompetenceId = 134L;

    public static AssessmentResultJpaEntity validSimpleAssessmentResultEntity(Long maturityLevelId, Boolean isValid) {
        return new AssessmentResultJpaEntity(
            UUID.randomUUID(),
            assessmentEntityWithKit(),
            maturityLevelId,
            isValid,
            LocalDateTime.now()
        );
    }

    private static AssessmentJpaEntity assessmentEntityWithKit() {
        assessmentCounter++;
        return new AssessmentJpaEntity(
            UUID.randomUUID(),
            "assessment-code" + assessmentCounter,
            "assessment-title" + assessmentCounter,
            kitId++,
            AssessmentColor.getDefault().getId(),
            1L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            NOT_DELETED_DELETION_TIME,
            Boolean.FALSE
        );
    }

    public static SubjectValueJpaEntity subjectValueWithMaturityLevel(AssessmentResultJpaEntity assessmentResultJpaEntity, Long maturityLevelId) {
        return new SubjectValueJpaEntity(
            UUID.randomUUID(),
            assessmentResultJpaEntity,
            subjectId++,
            5L
        );
    }

    public static QualityAttributeValueJpaEntity qualityAttributeWithMaturityLevel(AssessmentResultJpaEntity assessmentResultJpaEntity, Long maturityLevelId) {
        return new QualityAttributeValueJpaEntity(
            UUID.randomUUID(),
            assessmentResultJpaEntity,
            qualityAttributeId++,
            maturityLevelId
        );
    }

    public static QualityAttributeDto createQualityAttributeDto(Long qualityAttributeId) {
        return new QualityAttributeDto(
            qualityAttributeId,
            1
        );
    }

    public static SubjectDto createSubjectDto(Long subjectId, List<QualityAttributeValueJpaEntity> qualityAttributes) {
        return new SubjectDto(
            subjectId,
            qualityAttributes.stream()
                .map(qav -> createQualityAttributeDto(qav.getQualityAttributeId()))
                .toList()
        );
    }

    public static QuestionDto createQuestionDto(Long questionId, Long maturityLevelId, Long... qavIds) {
        return new QuestionDto(
            questionId,
            Arrays.stream(qavIds)
                .map(qavId -> createQuestionImpactDto(maturityLevelId, qavId))
                .toList()
        );
    }

    public static QuestionImpactDto createQuestionImpactDto(Long maturityLevelId, Long qualityAttributeId) {
        return new QuestionImpactDto(
            questionImpactId++,
            maturityLevelId.intValue(),
            maturityLevelId,
            qualityAttributeId
        );
    }

    public static AnswerJpaEntity answerEntityWithAnswerOptionAndIsNotApplicable(AssessmentResultJpaEntity assessmentResultJpaEntity, Long questionId, Long answerOptionId, Boolean isNotApplicable) {
        return new AnswerJpaEntity(
            UUID.randomUUID(),
            assessmentResultJpaEntity,
            1L,
            questionId,
            answerOptionId,
            isNotApplicable
        );
    }

    public static AnswerOptionDto createAnswerOptionDto(Long answerOptionId, Long questionId, List<QuestionImpactDto> questionImpactDtos) {
        return new AnswerOptionDto(
            answerOptionId,
            questionId,
            questionImpactDtos.stream()
                .map(qi -> createAnswerOptionImpactDto(qi))
                .toList()
        );
    }

    public static AnswerOptionImpactDto createAnswerOptionImpactDto(QuestionImpactDto questionImpactDto) {
        return new AnswerOptionImpactDto(
            answerOptionImpactId++,
            1.0,
            questionImpactDto
        );
    }

    public static MaturityLevelDto createMaturityLevelDto(Long maturityLevelId) {
        return new MaturityLevelDto(
            maturityLevelId,
            maturityLevelId.intValue(),
            createCompetences(maturityLevelId)
        );
    }

    private static List<LevelCompetenceDto> createCompetences(Long maturityLevelId) {
        List<LevelCompetenceDto> levelCompetenceDtos = new ArrayList<>();
        for (int i = 0; i < maturityLevelId - 1; i++) {
            var levelCompetenceDto = new LevelCompetenceDto(levelCompetenceId++, (i + 1) * 10, Long.valueOf(i));
            levelCompetenceDtos.add(levelCompetenceDto);
        }
        return levelCompetenceDtos;
    }

    record Context(
        AssessmentResultJpaEntity assessmentResultEntity,
        List<SubjectValueJpaEntity> subjectValues,
        List<QualityAttributeValueJpaEntity> qualityAttributeValues,
        List<SubjectDto> subjectDtos,
        List<QuestionDto> questionDtos,
        List<AnswerJpaEntity> answerEntities,
        List<AnswerOptionDto> answerOptionDtos
    ) {
    }

}
