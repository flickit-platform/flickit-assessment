package org.flickit.assessment.core.application.service.insight.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.AssessmentMode;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.insight.AssessmentInsight;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelPort;
import org.flickit.assessment.core.application.port.out.subject.CountSubjectsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import static org.flickit.assessment.core.application.service.insight.assessment.AssessmentInsightBuilderHelper.buildAssessmentInsight;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CreateAssessmentInsightHelper {

    private final GetAssessmentProgressPort getAssessmentProgressPort;
    private final LoadMaturityLevelPort loadMaturityLevelPort;
    private final CountSubjectsPort countSubjectsPort;

    public AssessmentInsight createAssessmentInsight(AssessmentResult assessmentResult, Locale locale) {
        var progress = getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId());
        int questionsCount = progress.questionsCount();
        int answersCount = progress.answersCount();
        int confidenceValue = assessmentResult.getConfidenceValue() != null
            ? (int) Math.ceil(assessmentResult.getConfidenceValue())
            : 0;
        var maturityLevelTitle = loadMaturityLevelPort.load(assessmentResult.getMaturityLevel().getId(),
                assessmentResult.getAssessment().getId())
            .getTitle();
        var assessmentInsightParam = new AssessmentInsightParam(assessmentResult.getAssessment().getMode(),
            maturityLevelTitle,
            questionsCount,
            answersCount,
            confidenceValue,
            locale);

        var subjectCount = countSubjectsPort.countSubjects(assessmentResult.getKitVersionId());
        String insight = buildAssessmentInsight(toAssessmentInsightBulderParam(assessmentInsightParam, subjectCount, locale));
        return toAssessmentInsight(assessmentResult.getId(), insight);
    }

    private static AssessmentInsightBuilderHelper.Param toAssessmentInsightBulderParam(AssessmentInsightParam param, int subjectCount, Locale locale) {
        return new AssessmentInsightBuilderHelper.Param(param.maturityLevelTitle,
            param.questionsCount,
            param.answersCount,
            param.confidenceValue,
            param.mode(),
            subjectCount,
            locale);
    }

    AssessmentInsight toAssessmentInsight(UUID assessmentResultId, String insight) {
        return new AssessmentInsight(null,
            assessmentResultId,
            insight,
            LocalDateTime.now(),
            LocalDateTime.now(),
            null,
            false);
    }

    record AssessmentInsightParam(AssessmentMode mode,
                                  String maturityLevelTitle,
                                  int questionsCount,
                                  int answersCount,
                                  int confidenceValue,
                                  Locale locale) {
    }
}
