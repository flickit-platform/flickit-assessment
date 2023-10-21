package org.flickit.flickitassessmentcore.adapter.out.calculate;

import org.flickit.flickitassessmentcore.adapter.out.persistence.answer.AnswerJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue.QualityAttributeValueJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.subjectvalue.SubjectValueJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.rest.answeroption.AnswerOptionDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.answeroptionimpact.AnswerOptionImpactDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.maturitylevel.MaturityLevelDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.qualityattribute.QualityAttributeDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.question.QuestionDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.questionimpact.QuestionImpactDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.subject.SubjectDto;
import org.flickit.flickitassessmentcore.application.domain.AnswerOptionImpact;
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

    public static AssessmentResultJpaEntity validSimpleAssessmentResultEntity(Long maturityLevelId) {
        return new AssessmentResultJpaEntity(
            UUID.randomUUID(),
            assessmentEntityWithKit(),
            maturityLevelId,
            Boolean.TRUE,
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

    public static QuestionDto createQuestionDto(Long questionId) {
        return new QuestionDto(
            questionId,
            List.of(
                createQuestionImpactDto(questionId, 4L),
                createQuestionImpactDto(questionId, 5L)
            )
        );
    }

    public static QuestionImpactDto createQuestionImpactDto(Long maturityLevelId, Long questionId) {
        return new QuestionImpactDto(
            questionImpactId++,
            1,
            maturityLevelId,
            questionId
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

    public static AnswerOptionDto createAnswerOptionDto(Long answerOptionId, Long questionId, QuestionImpactDto questionImpactDto) {
        return new AnswerOptionDto(
            answerOptionId,
            questionId,
            List.of(
                createAnswerOptionImpactDto(questionImpactDto)
            )
        );
    }

    public static AnswerOptionImpactDto createAnswerOptionImpactDto(QuestionImpactDto questionImpactDto) {
        return new AnswerOptionImpactDto(
            answerOptionImpactId++,
            1D,
            questionImpactDto
        );
    }

    public static MaturityLevelDto createMaturityLevelDto(Long maturityLevelId) {
        return new MaturityLevelDto(
            maturityLevelId,
            maturityLevelId.intValue(),
            new ArrayList<>()
        );
    }

}
