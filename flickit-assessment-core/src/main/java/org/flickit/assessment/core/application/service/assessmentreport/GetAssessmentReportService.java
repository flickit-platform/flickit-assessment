package org.flickit.assessment.core.application.service.assessmentreport;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.InvalidStateException;
import org.flickit.assessment.common.util.MathUtils;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.domain.report.AssessmentReportItem;
import org.flickit.assessment.core.application.domain.report.AssessmentReportItem.AssessmentKitItem;
import org.flickit.assessment.core.application.domain.report.AssessmentSubjectReportItem;
import org.flickit.assessment.core.application.domain.report.AttributeReportItem;
import org.flickit.assessment.core.application.domain.report.QuestionnaireReportItem;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentReportUseCase;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentReportUseCase.AdviceItem.Level;
import org.flickit.assessment.core.application.port.out.adviceitem.LoadAdviceItemsPort;
import org.flickit.assessment.core.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentQuestionsPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentReportInfoPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.*;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.exception.api.ErrorCodes.REPORT_UNPUBLISHED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_REPORT_REPORT_NOT_PUBLISHED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentReportService implements GetAssessmentReportUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentReportInfoPort loadAssessmentReportInfoPort;
    private final LoadAssessmentReportPort loadAssessmentReportPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadAssessmentQuestionsPort loadAssessmentQuestionsPort;
    private final LoadAdviceNarrationPort loadAdviceNarrationPort;
    private final LoadAdviceItemsPort loadAdviceItemsPort;

    @Override
    public Result getAssessmentReport(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_GRAPHICAL_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        validateAssessmentResultPort.validate(param.getAssessmentId());

        var assessmentReport = loadAssessmentReportPort.load(param.getAssessmentId());
        var published = assessmentReport.map(AssessmentReport::isPublished)
            .orElse(false);
        var reportMetadata = assessmentReport.map(AssessmentReport::getMetadata)
            .orElse(new AssessmentReportMetadata(null, null, null, null));

        validateReportPublication(param, published);

        var assessmentReportInfo = loadAssessmentReportInfoPort.load(param.getAssessmentId());

        var attributeMeasuresMap = buildAttributeMeasures(param.getAssessmentId(), assessmentReportInfo);

        return buildResult(assessmentReportInfo, attributeMeasuresMap, reportMetadata, param);
    }

    private void validateReportPublication(Param param, boolean published) {
        if (!published &&
            !assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_REPORT_PREVIEW))
            throw new InvalidStateException(REPORT_UNPUBLISHED, GET_ASSESSMENT_REPORT_REPORT_NOT_PUBLISHED);
    }

    private Result buildResult(LoadAssessmentReportInfoPort.Result assessmentReportInfo,
                               Map<Long, List<AttributeMeasure>> attributeMeasuresMap,
                               AssessmentReportMetadata metadata,
                               Param param) {
        var assessment = assessmentReportInfo.assessment();
        var assessmentKitItem = assessment.assessmentKit();

        var maturityLevels = toMaturityLevels(assessmentKitItem);
        var attributesCount = countAttributes(assessmentReportInfo);
        var maturityLevelMap = maturityLevels.stream()
            .collect(toMap(MaturityLevel::id, Function.identity()));

        return new Result(toAssessment(assessment, assessmentKitItem, metadata, maturityLevels, attributesCount, maturityLevelMap),
            toSubjects(assessmentReportInfo.subjects(), maturityLevelMap, attributeMeasuresMap),
            toAdvice(assessment.assessmentResultId(), Locale.of(assessmentKitItem.language().name())),
            toAssessmentProcess(metadata),
            toPermissions(param),
            toLanguage(assessmentKitItem.language()));
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

    private Permissions toPermissions(Param param) {
        var canViewDashboard = assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD);
        return new Permissions(canViewDashboard);
    }

    private Language toLanguage(KitLanguage language) {
        return new Language(language.getCode());
    }

    private Map<Long, List<AttributeMeasure>> buildAttributeMeasures(UUID assessmentId, LoadAssessmentReportInfoPort.Result reportInfo) {
        var measures = reportInfo.assessment().assessmentKit().measures();
        Map<Long, Measure> idToMeasureMap = measures.stream()
            .collect(toMap(Measure::getId, Function.identity()));

        var questions = loadAssessmentQuestionsPort.loadApplicableQuestions(assessmentId);

        Map<Long, List<LoadAssessmentQuestionsPort.Result>> attrIdToQuestions = new HashMap<>();

        for (LoadAssessmentQuestionsPort.Result r : questions) {
            for (QuestionImpact impact : r.question().getImpacts()) {
                var attrId = impact.getAttributeId();
                attrIdToQuestions
                    .computeIfAbsent(attrId, k -> new ArrayList<>())
                    .add(r);
            }
        }

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
        var impactPercentage = attributeMaxPossibleScore != 0
            ? (measureMaxPossibleScore / attributeMaxPossibleScore) * 100
            : 0.0;

        var gainedScore = questions.stream()
            .mapToDouble(q -> (q.answer() != null && q.answer().getSelectedOption() != null)
                ? q.answer().getSelectedOption().getValue() * q.weight()
                : 0.0)
            .sum();

        var missedScore = measureMaxPossibleScore - gainedScore;

        return new AttributeMeasure(measure.getTitle(),
            MathUtils.round(impactPercentage, 2),
            MathUtils.round(measureMaxPossibleScore, 2),
            MathUtils.round(gainedScore, 2),
            MathUtils.round(missedScore, 2),
            MathUtils.round((gainedScore / attributeMaxPossibleScore) * 100, 2),
            MathUtils.round((missedScore / attributeMaxPossibleScore) * 100, 2)
        );
    }

    record QuestionDto(long id, double weight, long measureId, Answer answer) {
    }
}
