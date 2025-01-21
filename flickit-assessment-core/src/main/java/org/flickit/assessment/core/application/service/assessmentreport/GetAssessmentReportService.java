package org.flickit.assessment.core.application.service.assessmentreport;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentReportUseCase;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attributevalue.LoadAttributeValuesPort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.questionnaire.LoadQuestionnairesByAssessmentIdPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.LoadSubjectValuesPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_ID_NOT_FOUND;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentReportService implements GetAssessmentReportUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentReportPort loadAssessmentReportPort;
    private final LoadAssessmentPort loadAssessmentPort;
    private final LoadAssessmentInsightPort loadAssessmentInsightPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final LoadQuestionnairesByAssessmentIdPort loadQuestionnairesByAssessmentIdPort;
    private final LoadSubjectValuesPort loadSubjectValuesPort;
    private final LoadAttributeValuesPort loadAttributeValuesPort;

    @Override
    public Result getAssessmentReport(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.MANAGE_REPORT_METADATA))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessment = loadAssessmentPort.getAssessmentById(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));
        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));
        var insight = loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));
        var report = loadAssessmentReportPort.load(param.getAssessmentId())
            .orElse(new AssessmentReport(null, null, new AssessmentReportMetadata(null, null, null, null)));
        var questionnaires = loadQuestionnairesByAssessmentIdPort.loadAllByAssessmentId(new LoadQuestionnairesByAssessmentIdPort.Param(assessmentResult, Integer.MAX_VALUE, 0));
        var subjectValues = loadSubjectValuesPort.loadByAssessmentId(assessment.getId());
        var maturityLevels = loadMaturityLevelsPort.loadByKitVersionId(assessment.getAssessmentKit().getKitVersion())
            .stream().map(this::mapToLevel).toList();
        var attributes = loadAttributeValuesPort.load(assessment.getId());
        var maturityLevelsMap = maturityLevels.stream().collect(toMap(MaturityLevel::id, Function.identity()));

        return mapToResult(assessment, assessmentResult, insight, report.getMetadata(), maturityLevels, maturityLevelsMap, subjectValues, questionnaires, attributes);
    }

    private Result mapToResult(org.flickit.assessment.core.application.domain.Assessment assessment,
                               AssessmentResult assessmentResult,
                               AssessmentInsight insight,
                               AssessmentReportMetadata metadata,
                               List<MaturityLevel> maturityLevels,
                               Map<Long, MaturityLevel> maturityLevelsMap,
                               List<SubjectValue> subjectValues,
                               PaginatedResponse<QuestionnaireListItem> questionnaireListItems,
                               List<AttributeValue> attributeValues) {
        List<Attribute> attributes = attributeValues.stream().map(attributeValue -> mapToAttribute(attributeValue, maturityLevelsMap)).toList();
        var subjects = subjectValues.stream().map(sv -> {
            var subject = sv.getSubject();
            return new Subject(subject.getId(),
                subject.getTitle(),
                subject.getIndex(),
                subject.getDescription(),
                sv.getConfidenceValue(),
                maturityLevelsMap.get(sv.getMaturityLevel().getId()),
                null //TODO
            );
        }).toList();

        var questionnaires = questionnaireListItems.getItems().stream()
            .map(this::toQuestionnaire)
            .toList();


        return new Result(
            toAssessment(assessment, metadata, insight, assessmentResult, maturityLevelsMap, maturityLevels, questionnaireListItems, attributes.size()),
            subjects,
            questionnaires,
            metadata.steps(),
            metadata.participants());
    }

    private Questionnaire toQuestionnaire(QuestionnaireListItem questionnaire) {
        return new Questionnaire(questionnaire.id(),
            questionnaire.title(),
            questionnaire.description(),
            questionnaire.index(),
            questionnaire.questionCount());
    }

    private Attribute mapToAttribute(AttributeValue attributeValue, Map<Long, MaturityLevel> maturityLevelsMap) {
        var attribute = attributeValue.getAttribute();
        return new Attribute(
            attribute.getId(),
            attribute.getTitle(),
            null,
            attribute.getDescription(),
            attribute.getIndex(),
            attributeValue.getConfidenceValue(),
            maturityLevelsMap.get(attributeValue.getMaturityLevel().getId()));
    }

    private Assessment toAssessment(org.flickit.assessment.core.application.domain.Assessment assessment,
                                    AssessmentReportMetadata metadata,
                                    AssessmentInsight insight,
                                    AssessmentResult assessmentResult,
                                    Map<Long, MaturityLevel> maturityLevelsMap,
                                    List<MaturityLevel> maturityLevels,
                                    PaginatedResponse<QuestionnaireListItem> questionnaireListItems,
                                    int attributesCount) {
        return new Assessment(assessment.getTitle(),
            metadata.intro(),
            insight.getInsight(),
            mapToKit(assessment.getAssessmentKit(), maturityLevels, metadata, questionnaireListItems, attributesCount),
            maturityLevelsMap.get(assessmentResult.getMaturityLevel().getId()),
            assessmentResult.getConfidenceValue(),
            assessment.getCreationTime()
        );
    }

    private MaturityLevel mapToLevel(org.flickit.assessment.core.application.domain.MaturityLevel maturityLevel) {
        return new MaturityLevel(maturityLevel.getId(),
            maturityLevel.getTitle(),
            maturityLevel.getIndex(),
            maturityLevel.getValue(),
            maturityLevel.getDescription());
    }

    private AssessmentKit mapToKit(org.flickit.assessment.core.application.domain.AssessmentKit assessmentKit,
                                   List<MaturityLevel> maturityLevels,
                                   AssessmentReportMetadata metadata,
                                   PaginatedResponse<QuestionnaireListItem> questionnaireListItems,
                                   int attributesCount) {
        int questionsCount = questionnaireListItems.getItems().stream()
            .mapToInt(QuestionnaireListItem::questionCount)
            .sum();
        return new AssessmentKit(assessmentKit.getId(),
            assessmentKit.getTitle(),
            maturityLevels.size(),
            questionsCount,
            questionnaireListItems.getTotal(),
            attributesCount,
            metadata.prosAndCons(),
            maturityLevels);
    }
}
