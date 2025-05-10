package org.flickit.assessment.core.application.service.assessmentreport;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.util.MathUtils;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.domain.report.AssessmentReportItem;
import org.flickit.assessment.core.application.domain.report.AssessmentReportItem.AssessmentKitItem;
import org.flickit.assessment.core.application.domain.report.AssessmentSubjectReportItem;
import org.flickit.assessment.core.application.domain.report.AttributeReportItem;
import org.flickit.assessment.core.application.domain.report.QuestionnaireReportItem;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentPublicReportUseCase;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentPublicReportUseCase.AdviceItem.Level;
import org.flickit.assessment.core.application.port.out.adviceitem.LoadAdviceItemsPort;
import org.flickit.assessment.core.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentQuestionsPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentReportInfoPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.*;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_REPORT_LINK_HASH_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentPublicReportService implements GetAssessmentPublicReportUseCase {

    private final LoadAssessmentReportPort loadAssessmentReportPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAssessmentReportInfoPort loadAssessmentReportInfoPort;
    private final LoadAssessmentQuestionsPort loadAssessmentQuestionsPort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAdviceNarrationPort loadAdviceNarrationPort;
    private final LoadAdviceItemsPort loadAdviceItemsPort;

    @Override
    public Result getAssessmentPublicReport(Param param) {
        var report = loadAssessmentReportPort.loadByLinkHash(param.getLinkHash());
        var assessmentResult = loadAssessmentResultPort.load(report.getAssessmentResultId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));

        if (!isReportPublic(report) && !userHasAccess(param.getCurrentUserId(), assessmentResult.getAssessment().getId()))
            throw new ResourceNotFoundException(ASSESSMENT_REPORT_LINK_HASH_NOT_FOUND);

        return buildReport(report, assessmentResult, param.getCurrentUserId());
    }

    private static boolean isReportPublic(AssessmentReport report) {
        return Objects.equals(report.getVisibility(), VisibilityType.PUBLIC) && report.isPublished();
    }

    private boolean userHasAccess(UUID currentUserId, UUID assessmentId) {
        return currentUserId != null && assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_GRAPHICAL_REPORT);
    }

    private Result buildReport(AssessmentReport report, AssessmentResult assessmentResult, UUID currentUserId) {
        var reportMetadata = Optional.ofNullable(report.getMetadata())
            .orElse(new AssessmentReportMetadata(null, null, null, null));

        var assessmentId = assessmentResult.getAssessment().getId();
        var assessmentReportInfo = loadAssessmentReportInfoPort.load(assessmentId);
        var attributeMeasuresMap = buildAttributeMeasures(assessmentId, assessmentReportInfo);
        var permissions = buildPermissions(assessmentId, currentUserId);

        return buildResult(assessmentReportInfo, attributeMeasuresMap, reportMetadata, permissions);
    }

    private Permissions buildPermissions(UUID assessmentId, UUID currentUserId) {
        if (currentUserId == null)
            return new Permissions(false, false, false);
        var canViewDashboard = assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_DASHBOARD);
        var canShareReport = assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, GRANT_ACCESS_TO_REPORT);
        var canManageVisibility = assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, MANAGE_ASSESSMENT_REPORT_VISIBILITY);
        return new Permissions(canViewDashboard, canShareReport, canManageVisibility);
    }

    private Result buildResult(LoadAssessmentReportInfoPort.Result assessmentReportInfo,
                               Map<Long, List<AttributeMeasure>> attributeMeasuresMap,
                               AssessmentReportMetadata metadata,
                               Permissions permissions) {
        var assessment = assessmentReportInfo.assessment();
        var assessmentKitItem = assessment.assessmentKit();

        var maturityLevels = toMaturityLevels(assessmentKitItem);
        var attributesCount = countAttributes(assessmentReportInfo);
        var maturityLevelMap = maturityLevels.stream()
            .collect(toMap(MaturityLevel::id, Function.identity()));

        return new Result(toAssessment(assessment, assessmentKitItem, metadata, maturityLevels, attributesCount, maturityLevelMap),
            toSubjects(assessmentReportInfo.subjects(), maturityLevelMap, attributeMeasuresMap),
            toAdvice(assessment.assessmentResultId(), Locale.of(assessment.language().name())),
            toAssessmentProcess(metadata),
            permissions,
            toLanguage(assessment.language()));
    }

    private List<MaturityLevel> toMaturityLevels(AssessmentKitItem assessmentKitItem) {
        return assessmentKitItem.maturityLevels().stream()
            .map(l -> new MaturityLevel(l.getId(),
                l.getTitle(),
                l.getIndex(),
                l.getValue(),
                l.getDescription()))
            .toList();
    }

    private int countAttributes(LoadAssessmentReportInfoPort.Result assessmentReportInfo) {
        return assessmentReportInfo.subjects().stream()
            .flatMap(s -> s.attributes().stream())
            .collect(toSet())
            .size();
    }

    private Assessment toAssessment(AssessmentReportItem assessment,
                                    AssessmentKitItem assessmentKitItem,
                                    AssessmentReportMetadata metadata,
                                    List<MaturityLevel> levels,
                                    int attributesCount,
                                    Map<Long, MaturityLevel> maturityLevelMap) {
        return new Assessment(
            assessment.title(),
            metadata.intro(),
            assessment.insight(),
            metadata.prosAndCons(),
            toAssessmentKit(assessmentKitItem, attributesCount, levels),
            maturityLevelMap.get(assessment.maturityLevel().getId()),
            assessment.confidenceValue(),
            assessment.creationTime());
    }

    private AssessmentKit toAssessmentKit(AssessmentKitItem assessmentKit,
                                          int attributesCount,
                                          List<MaturityLevel> levels) {
        var questionnaires = assessmentKit.questionnaires().stream()
            .map(this::toQuestionnaire).toList();
        return new AssessmentKit(assessmentKit.id(),
            assessmentKit.title(),
            assessmentKit.maturityLevelCount(),
            assessmentKit.questionsCount(),
            assessmentKit.questionnaires().size(),
            attributesCount,
            questionnaires,
            levels);
    }

    private Questionnaire toQuestionnaire(QuestionnaireReportItem questionnaire) {
        return new Questionnaire(questionnaire.id(),
            questionnaire.title(),
            questionnaire.description(),
            questionnaire.index(),
            questionnaire.questionCount());
    }

    private List<Subject> toSubjects(List<AssessmentSubjectReportItem> subjects, Map<Long, MaturityLevel> maturityLevelMap,
                                     Map<Long, List<AttributeMeasure>> attributeMeasuresMap) {
        return subjects.stream()
            .map(subject -> toSubject(subject, maturityLevelMap, attributeMeasuresMap))
            .toList();
    }

    private Subject toSubject(AssessmentSubjectReportItem subject, Map<Long, MaturityLevel> maturityLevelMap,
                              Map<Long, List<AttributeMeasure>> attributeMeasuresMap) {
        var attributes = subject.attributes().stream()
            .map(attribute -> toAttribute(attribute, maturityLevelMap, attributeMeasuresMap.get(attribute.id())))
            .sorted(Comparator.comparingInt(Attribute::index))
            .toList();
        return new Subject(subject.id(),
            subject.title(),
            subject.index(),
            subject.insight(),
            subject.confidenceValue(),
            maturityLevelMap.get(subject.maturityLevel().getId()),
            attributes);
    }

    private Attribute toAttribute(AttributeReportItem attribute, Map<Long, MaturityLevel> maturityLevelMap,
                                  List<AttributeMeasure> attributeMeasures) {
        List<AttributeMeasure> sortedMeasures = attributeMeasures.stream()
            .sorted(Comparator.comparing(AttributeMeasure::impactPercentage).reversed())
            .toList();

        return new Attribute(attribute.id(),
            attribute.title(),
            attribute.description(),
            attribute.insight(),
            attribute.index(),
            attribute.weight(),
            attribute.confidenceValue(),
            maturityLevelMap.get(attribute.maturityLevel().getId()),
            sortedMeasures);
    }

    private Advice toAdvice(UUID assessmentResultId, Locale locale) {
        var narration = loadAdviceNarrationPort.load(assessmentResultId);
        var adviceItems = loadAdviceItemsPort.loadAll(assessmentResultId);
        return new Advice(narration, toAdviceItems(adviceItems, locale));
    }

    private List<AdviceItem> toAdviceItems(List<org.flickit.assessment.core.application.domain.AdviceItem> adviceItems, Locale locale) {
        return adviceItems.stream()
            .map(item -> new AdviceItem(item.getId(),
                item.getTitle(),
                item.getDescription(),
                new Level(item.getCost().getCode(), item.getCost().getTitle(locale)),
                new Level(item.getPriority().getCode(), item.getPriority().getTitle(locale)),
                new Level(item.getImpact().getCode(), item.getImpact().getTitle(locale))))
            .toList();
    }

    private AssessmentProcess toAssessmentProcess(AssessmentReportMetadata metadata) {
        return new AssessmentProcess(metadata.steps(), metadata.participants());
    }

    private Language toLanguage(KitLanguage language) {
        return new Language(language.getCode());
    }

    private Map<Long, List<AttributeMeasure>> buildAttributeMeasures(UUID assessmentId, LoadAssessmentReportInfoPort.Result reportInfo) {
        var measures = reportInfo.assessment().assessmentKit().measures();
        Map<Long, Measure> idToMeasureMap = measures.stream()
            .collect(toMap(Measure::getId, Function.identity()));

        var questions = loadAssessmentQuestionsPort.loadApplicableQuestions(assessmentId);

        Map<Long, Set<LoadAssessmentQuestionsPort.Result>> attrIdToQuestions = questions.stream()
            .flatMap(r -> r.question().getImpacts().stream()
                .map(QuestionImpact::getAttributeId)
                .distinct() // remove duplicate attributeIds per question
                .map(attributeId -> new AbstractMap.SimpleEntry<>(attributeId, r)))
            .collect(groupingBy(
                Map.Entry::getKey,
                mapping(Map.Entry::getValue, toSet())
            ));

        Map<Long, List<QuestionDto>> attrIdToQuestionDtos = attrIdToQuestions.entrySet().stream()
            .collect(toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream()
                    .map(r -> new QuestionDto(
                        r.question().getId(),
                        r.question().getAvgWeight(entry.getKey()),
                        r.question().getMeasure().getId(),
                        r.answer()))
                    .toList()
            ));

        return attrIdToQuestionDtos.entrySet().stream()
            .collect(toMap(
                Map.Entry::getKey,
                entry -> toAttributeMeasures(entry.getValue(), idToMeasureMap)
            ));
    }

    private List<AttributeMeasure> toAttributeMeasures(List<QuestionDto> attributeQuestions, Map<Long, Measure> idToMeasureMap) {
        var attributeMaxPossibleScore = attributeQuestions.stream()
            .mapToDouble(QuestionDto::weight)
            .sum();

        var measureIdToQuestions = attributeQuestions.stream()
            .collect(groupingBy(QuestionDto::measureId));

        var measureIdToMaxPossibleScore = attributeQuestions.stream()
            .collect(groupingBy(
                QuestionDto::measureId,
                summingDouble(QuestionDto::weight) // Sum weights for each measureId
            ));

        return measureIdToQuestions.entrySet().stream()
            .map(e -> buildMeasure(
                idToMeasureMap.get(e.getKey()),
                e.getValue(),
                measureIdToMaxPossibleScore.get(e.getKey()),
                attributeMaxPossibleScore))
            .toList();
    }

    private AttributeMeasure buildMeasure(Measure measure,
                                          List<QuestionDto> questions,
                                          double measureMaxPossibleScore,
                                          double attributeMaxPossibleScore) {
        assert measureMaxPossibleScore != 0.0;
        var impactPercentage = attributeMaxPossibleScore != 0
            ? (measureMaxPossibleScore / attributeMaxPossibleScore) * 100
            : 0.0;

        var gainedScore = questions.stream()
            .mapToDouble(q -> (q.answer() != null && q.answer().getSelectedOption() != null)
                ? q.answer().getSelectedOption().getValue() * q.weight()
                : 0.0)
            .sum();

        var missedScore = measureMaxPossibleScore - gainedScore;

        var gainedScorePercentage = attributeMaxPossibleScore == 0 ? 1 : (gainedScore / attributeMaxPossibleScore) * 100;
        var missedScorePercentage = attributeMaxPossibleScore == 0 ? 1 : (missedScore / attributeMaxPossibleScore) * 100;

        return new AttributeMeasure(measure.getTitle(),
            MathUtils.round(impactPercentage, 2),
            MathUtils.round(measureMaxPossibleScore, 2),
            MathUtils.round(gainedScore, 2),
            MathUtils.round(missedScore, 2),
            MathUtils.round(gainedScorePercentage, 2),
            MathUtils.round(missedScorePercentage, 2)
        );
    }

    record QuestionDto(long id, double weight, long measureId, Answer answer) {
    }
}
