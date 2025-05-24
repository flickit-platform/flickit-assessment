package org.flickit.assessment.core.application.service.assessmentreport;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.InvalidStateException;
import org.flickit.assessment.core.application.domain.AssessmentReport;
import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;
import org.flickit.assessment.core.application.domain.QuestionImpact;
import org.flickit.assessment.core.application.domain.VisibilityType;
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
import org.flickit.assessment.core.application.service.measure.CalculateMeasureHelper;
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
    private final CalculateMeasureHelper calculateMeasureHelper;

    @Override
    public Result getAssessmentReport(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_GRAPHICAL_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        validateAssessmentResultPort.validate(param.getAssessmentId());

        var assessmentReport = loadAssessmentReportPort.load(param.getAssessmentId());
        boolean published = assessmentReport.map(AssessmentReport::isPublished)
            .orElse(false);
        var reportMetadata = assessmentReport.map(AssessmentReport::getMetadata)
            .orElse(new AssessmentReportMetadata(null, null, null, null));
        var reportVisibility = assessmentReport.map(AssessmentReport::getVisibility)
                .orElse(VisibilityType.RESTRICTED);

        validateReportPublication(param, published);

        var assessmentReportInfo = loadAssessmentReportInfoPort.load(param.getAssessmentId());

        var attributeMeasuresMap = buildAttributeMeasures(param.getAssessmentId());

        return buildResult(assessmentReportInfo, attributeMeasuresMap, reportMetadata, param, published, reportVisibility);
    }

    private void validateReportPublication(Param param, boolean published) {
        if (!published &&
            !assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_REPORT_PREVIEW))
            throw new InvalidStateException(REPORT_UNPUBLISHED, GET_ASSESSMENT_REPORT_REPORT_NOT_PUBLISHED);
    }

    private Result buildResult(LoadAssessmentReportInfoPort.Result assessmentReportInfo,
                               Map<Long, List<AttributeMeasure>> attributeMeasuresMap,
                               AssessmentReportMetadata metadata,
                               Param param,
                               boolean published,
                               VisibilityType visibility) {
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
            toPermissions(param.getAssessmentId(), published, param.getCurrentUserId()),
            toLanguage(assessment.language()),
            visibility.name());
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
            toMode(assessment.mode()),
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
            .map(attribute -> toAttribute(attribute, maturityLevelMap, attributeMeasuresMap.getOrDefault(attribute.id(), List.of())))
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

    private Permissions toPermissions(UUID assessmentId, boolean published, UUID currentUserId) {
        var canViewDashboard = assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_DASHBOARD);
        var canShareReport = published && assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, GRANT_ACCESS_TO_REPORT);
        var canManageVisibility = published && assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, MANAGE_ASSESSMENT_REPORT_VISIBILITY);
        return new Permissions(canViewDashboard, canShareReport, canManageVisibility);
    }

    private Language toLanguage(KitLanguage language) {
        return new Language(language.getCode());
    }

    private Mode toMode(AssessmentMode mode) {
        return new Mode(mode.getCode());
    }

    private Map<Long, List<AttributeMeasure>> buildAttributeMeasures(UUID assessmentId) {
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

        Map<Long, List<CalculateMeasureHelper.QuestionDto>> attrIdToQuestionDtos = attrIdToQuestions.entrySet().stream()
            .collect(toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream()
                    .map(r -> new CalculateMeasureHelper.QuestionDto(
                        r.question().getId(),
                        r.question().getAvgWeight(entry.getKey()),
                        r.question().getMeasure().getId(),
                        r.answer()))
                    .toList()
            ));

        return attrIdToQuestionDtos.entrySet().stream()
            .collect(toMap(
                Map.Entry::getKey,
                entry -> calculateMeasureHelper.calculateMeasures(assessmentId, entry.getValue()).stream()
                    .map(this::mapToResultMeasure)
                    .toList()
            ));
    }

    private AttributeMeasure mapToResultMeasure(CalculateMeasureHelper.MeasureDto measureDto) {
        return new AttributeMeasure(measureDto.title(),
            measureDto.impactPercentage(),
            measureDto.maxPossibleScore(),
            measureDto.gainedScore(),
            measureDto.missedScore(),
            measureDto.gainedScorePercentage(),
            measureDto.missedScorePercentage());
    }
}
