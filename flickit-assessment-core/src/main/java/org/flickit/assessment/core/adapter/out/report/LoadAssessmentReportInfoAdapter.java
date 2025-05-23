package org.flickit.assessment.core.adapter.out.report;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.AttributeTranslation;
import org.flickit.assessment.common.application.domain.kit.translation.SubjectTranslation;
import org.flickit.assessment.common.application.domain.kitcustom.KitCustomData;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.util.JsonUtils;
import org.flickit.assessment.core.adapter.out.persistence.kit.assessmentkit.AssessmentKitMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.measure.MeasureMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.questionnaire.QuestionnaireMapper;
import org.flickit.assessment.core.application.domain.AssessmentMode;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.report.AssessmentReportItem;
import org.flickit.assessment.core.application.domain.report.AssessmentSubjectReportItem;
import org.flickit.assessment.core.application.domain.report.AttributeReportItem;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentReportInfoPort;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaRepository;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.SubjectIdAttributeValueView;
import org.flickit.assessment.data.jpa.core.insight.assessment.AssessmentInsightJpaEntity;
import org.flickit.assessment.data.jpa.core.insight.assessment.AssessmentInsightJpaRepository;
import org.flickit.assessment.data.jpa.core.insight.attribute.AttributeInsightJpaEntity;
import org.flickit.assessment.data.jpa.core.insight.attribute.AttributeInsightJpaRepository;
import org.flickit.assessment.data.jpa.core.insight.subject.SubjectInsightJpaEntity;
import org.flickit.assessment.data.jpa.core.insight.subject.SubjectInsightJpaRepository;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaEntity;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.kitcustom.KitCustomJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.data.jpa.kit.measure.MeasureJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel.MaturityLevelMapper.mapToDomainModel;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Slf4j
@Component
@AllArgsConstructor
public class LoadAssessmentReportInfoAdapter implements LoadAssessmentReportInfoPort {

    private final AssessmentJpaRepository assessmentRepository;
    private final AssessmentResultJpaRepository assessmentResultRepo;
    private final SubjectValueJpaRepository subjectValueRepo;
    private final AssessmentKitJpaRepository assessmentKitRepository;
    private final MaturityLevelJpaRepository maturityLevelRepository;
    private final SubjectJpaRepository subjectRepository;
    private final MeasureJpaRepository measureRepository;
    private final AttributeValueJpaRepository attributeValueJpaRepository;
    private final AssessmentInsightJpaRepository assessmentInsightRepository;
    private final QuestionnaireJpaRepository questionnaireRepository;
    private final AttributeInsightJpaRepository attributeInsightRepository;
    private final SubjectInsightJpaRepository subjectInsightRepository;
    private final KitCustomJpaRepository kitCustomRepository;

    @Override
    public Result load(UUID assessmentId) {
        if (!assessmentRepository.existsByIdAndDeletedFalse(assessmentId))
            throw new ResourceNotFoundException(GET_ASSESSMENT_REPORT_ASSESSMENT_ID_NOT_FOUND);

        var assessmentResultEntity = assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_REPORT_ASSESSMENT_RESULT_NOT_FOUND));

        var assessment = assessmentResultEntity.getAssessment();
        var assessmentKitEntity = assessmentKitRepository.findById(assessment.getAssessmentKitId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_REPORT_ASSESSMENT_KIT_NOT_FOUND));

        var assessmentInsight = assessmentInsightRepository.findByAssessmentResultId(assessmentResultEntity.getId())
            .map(AssessmentInsightJpaEntity::getInsight)
            .orElse(null);

        var kitVersionId = assessmentResultEntity.getKitVersionId();
        var translationLanguage = Objects.equals(assessmentResultEntity.getLangId(), assessmentKitEntity.getLanguageId()) ? null
            : KitLanguage.valueOfById(assessmentResultEntity.getLangId());

        var maturityLevels = maturityLevelRepository.findAllByKitVersionIdOrderByIndex(kitVersionId).stream()
            .map(e -> mapToDomainModel(e, translationLanguage))
            .toList();

        var idToMaturityLevel = maturityLevels.stream()
            .collect(toMap(MaturityLevel::getId, Function.identity()));

        var assessmentReportItem = new AssessmentReportItem(assessmentId,
            assessmentResultEntity.getId(),
            assessment.getTitle(),
            assessmentInsight,
            buildAssessmentKitItem(kitVersionId, assessmentKitEntity, maturityLevels, translationLanguage),
            idToMaturityLevel.get(assessmentResultEntity.getMaturityLevelId()),
            Objects.requireNonNullElse(assessmentResultEntity.getConfidenceValue(), 0.0),
            KitLanguage.valueOfById(assessmentResultEntity.getLangId()),
            AssessmentMode.valueOfById(assessment.getMode()),
            assessment.getCreationTime()
        );

        var subjects = buildSubjectReportItems(assessmentResultEntity, idToMaturityLevel, translationLanguage);

        return new Result(assessmentReportItem, subjects);
    }

    private AssessmentReportItem.AssessmentKitItem buildAssessmentKitItem(long kitVersionId,
                                                                          AssessmentKitJpaEntity assessmentKitEntity,
                                                                          List<MaturityLevel> maturityLevels,
                                                                          @Nullable KitLanguage language) {
        var questionnaireViews = questionnaireRepository.findAllWithQuestionCountByKitVersionId(kitVersionId, null).getContent();
        var questionnaireReportItems = questionnaireViews.stream()
            .map(view -> QuestionnaireMapper.mapToReportItem(view, language))
            .toList();

        var measures = measureRepository.findAllByKitVersionId(kitVersionId).stream()
            .map(measure -> MeasureMapper.mapToDomainModel(measure, language))
            .toList();

        return AssessmentKitMapper.mapToReportItem(assessmentKitEntity,
            maturityLevels,
            questionnaireReportItems,
            measures,
            language);
    }

    private List<AssessmentSubjectReportItem> buildSubjectReportItems(AssessmentResultJpaEntity assessmentResult,
                                                                      Map<Long, MaturityLevel> idToMaturityLevel,
                                                                      @Nullable KitLanguage language) {
        List<SubjectValueJpaEntity> subjectValueEntities = subjectValueRepo.findByAssessmentResultId(assessmentResult.getId());

        Set<Long> subjectIds = subjectValueEntities.stream()
            .map(SubjectValueJpaEntity::getSubjectId)
            .collect(Collectors.toSet());

        var subjectIdToSubjectValue = subjectValueEntities.stream()
            .collect(Collectors.toMap(SubjectValueJpaEntity::getSubjectId, Function.identity()));

        var subjectIdToAttributeValueMap = attributeValueJpaRepository.findByAssessmentResultIdAndSubjectIdInOrderByIndex(
                assessmentResult.getId(), subjectIds).stream()
            .collect(groupingBy(SubjectIdAttributeValueView::getSubjectId));

        var subjectEntities = subjectRepository.findAllByKitVersionIdOrderByIndex(assessmentResult.getKitVersionId());

        var attributeIdToInsightMap = attributeInsightRepository.findByAssessmentResultId(assessmentResult.getId()).stream()
            .collect(toMap(AttributeInsightJpaEntity::getAttributeId, Function.identity()));

        var subjectIdToInsightMap = subjectInsightRepository.findByAssessmentResultId(assessmentResult.getId()).stream()
            .collect(toMap(SubjectInsightJpaEntity::getSubjectId, Function.identity()));

        var attributeEntities = subjectEntities.stream()
            .map(e -> subjectIdToAttributeValueMap.get(e.getId()))
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .map(SubjectIdAttributeValueView::getAttribute)
            .toList();

        var attributeIdToWeight = getAttributeIdToWeightMap(attributeEntities,
            assessmentResult.getAssessment().getAssessmentKitId(),
            assessmentResult.getAssessment().getKitCustomId());

        return subjectEntities.stream()
            .map(e -> {
                var translation = getSubjectTranslation(e, language);
                Long maturityLevelId = subjectIdToSubjectValue.get(e.getId()).getMaturityLevelId();
                MaturityLevel subjectMaturityLevel = idToMaturityLevel.get(maturityLevelId);
                var attributeValues = subjectIdToAttributeValueMap.get(e.getId());
                var insight = Optional.ofNullable(subjectIdToInsightMap.get(e.getId()))
                    .map(SubjectInsightJpaEntity::getInsight)
                    .orElse(null);
                return new AssessmentSubjectReportItem(e.getId(),
                    translation.titleOrDefault(e.getTitle()),
                    e.getIndex(),
                    insight,
                    Objects.requireNonNullElse(subjectIdToSubjectValue.get(e.getId()).getConfidenceValue(), 0.0),
                    subjectMaturityLevel,
                    attributeValues == null ? List.of() : attributeValues.stream()
                        .map(x -> buildAttributeReportItem(idToMaturityLevel,
                            x,
                            attributeIdToInsightMap.get(x.getAttribute().getId()),
                            attributeIdToWeight.get(x.getAttribute().getId()),
                            language))
                        .toList());
            }).toList();
    }

    private AttributeReportItem buildAttributeReportItem(Map<Long, MaturityLevel> idToMaturityLevel,
                                                         SubjectIdAttributeValueView attributeValueView,
                                                         AttributeInsightJpaEntity attributeInsight,
                                                         int attributeWeight,
                                                         @Nullable KitLanguage language) {
        var attribute = attributeValueView.getAttribute();
        var translation = getAttributeTranslation(attribute, language);
        var attributeValue = attributeValueView.getAttributeValue();
        var maturityLevel = idToMaturityLevel.get(attributeValue.getMaturityLevelId());
        var insight = getAttributeInsight(attributeInsight);
        return new AttributeReportItem(attribute.getId(),
            translation.titleOrDefault(attribute.getTitle()),
            translation.descriptionOrDefault(attribute.getDescription()),
            insight,
            attribute.getIndex(),
            attributeWeight,
            Objects.requireNonNullElse(attributeValue.getConfidenceValue(), 0.0),
            maturityLevel);
    }

    private String getAttributeInsight(AttributeInsightJpaEntity insight) {
        if (insight == null || (insight.getAiInsight() == null && insight.getAssessorInsight() == null))
            return null;
        if (insight.getAssessorInsight() == null ||
            insight.getAiInsightTime() != null && insight.getAiInsightTime().isAfter(insight.getAssessorInsightTime())) {
            return insight.getAiInsight();
        }
        return insight.getAssessorInsight();
    }

    private Map<Long, Integer> getAttributeIdToWeightMap(List<AttributeJpaEntity> attributeEntities, long kitId, Long kitCustomId) {
        if (kitCustomId == null)
            return attributeEntities.stream()
                .collect(Collectors.toMap(AttributeJpaEntity::getId, AttributeJpaEntity::getWeight));

        var kitCustomEntity = kitCustomRepository.findByIdAndKitId(kitCustomId, kitId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_CUSTOM_ID_NOT_FOUND));

        KitCustomData kitCustomData = JsonUtils.fromJson(kitCustomEntity.getCustomData(), KitCustomData.class);

        if (kitCustomData == null || kitCustomData.attributes() == null)
            return attributeEntities.stream()
                .collect(Collectors.toMap(AttributeJpaEntity::getId, AttributeJpaEntity::getWeight));

        Map<Long, Integer> attributeIdToCustomWeight = kitCustomData.attributes().stream()
            .collect(Collectors.toMap(KitCustomData.Attribute::id, KitCustomData.Attribute::weight));

        return attributeEntities.stream()
            .collect(Collectors.toMap(
                AttributeJpaEntity::getId,
                e -> attributeIdToCustomWeight.getOrDefault(e.getId(), e.getWeight())
            ));
    }

    private SubjectTranslation getSubjectTranslation(SubjectJpaEntity entity, @Nullable KitLanguage language) {
        var translation = new SubjectTranslation(null, null);
        if (language != null) {
            var translations = JsonUtils.fromJsonToMap(entity.getTranslations(), KitLanguage.class, SubjectTranslation.class);
            translation = translations.getOrDefault(language, translation);
        }
        return translation;
    }

    private AttributeTranslation getAttributeTranslation(AttributeJpaEntity entity, @Nullable KitLanguage language) {
        var translation = new AttributeTranslation(null, null);
        if (language != null) {
            var translations = JsonUtils.fromJsonToMap(entity.getTranslations(), KitLanguage.class, AttributeTranslation.class);
            translation = translations.getOrDefault(language, translation);
        }
        return translation;
    }
}
