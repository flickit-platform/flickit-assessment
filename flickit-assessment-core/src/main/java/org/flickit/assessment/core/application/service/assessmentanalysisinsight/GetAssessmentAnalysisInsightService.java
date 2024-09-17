package org.flickit.assessment.core.application.service.assessmentanalysisinsight;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.InvalidContentException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.assessmentanalysisinsight.GetAssessmentAnalysisInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentanalysis.LoadAssessmentAnalysisPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ASSESSMENT_AI_ANALYSIS;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_ANALYSIS_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_ANALYSIS_UNABLE_TO_PARSE_JSON;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_ANALYSIS_AI_IS_DISABLED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentAnalysisInsightService implements GetAssessmentAnalysisInsightUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAssessmentAnalysisPort loadAssessmentAnalysisPort;
    private final AppAiProperties appAiProperties;

    @Override
    public Result getAssessmentAnalysisInsight(Param param) {
        if(!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        int type = AnalysisType.valueOf(param.getType()).getId();
        AssessmentResult assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_ANALYSIS_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND));

        boolean editable = assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ASSESSMENT_AI_ANALYSIS);
        Optional<AssessmentAnalysis> assessmentAnalysis = loadAssessmentAnalysisPort.load(assessmentResult.getId(), type);

        if (assessmentAnalysis.isEmpty()) {
            if (!appAiProperties.isEnabled()) {
                var aiAnalysis =
                        new Result.AnalysisInsight(new Result.AnalysisInsight.Message(MessageBundle.message(ASSESSMENT_ANALYSIS_AI_IS_DISABLED)),
                                null);
                return new Result(aiAnalysis, null, false);
            }
            return new Result(null, null, editable);
        }

        AssessmentAnalysis analysis = assessmentAnalysis.get();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (analysis.getAssessorAnalysis() == null) {
                AssessmentAnalysisInsight aiInsight = objectMapper.readValue(analysis.getAiAnalysis(),
                        AssessmentAnalysisInsight.class);
                var aiAnalysis = new Result.AnalysisInsight(new Result.AnalysisInsight.AiInsight(aiInsight),
                        analysis.getAiAnalysisTime());
                return new Result(aiAnalysis, null, editable);
            }

            AssessmentAnalysisInsight assessorInsight = objectMapper.readValue(analysis.getAssessorAnalysis(),
                    AssessmentAnalysisInsight.class);
            var assessorAnalysis = new Result.AnalysisInsight(new Result.AnalysisInsight.AssessorInsight(assessorInsight),
                    analysis.getAssessorAnalysisTime());
            return new Result(null, assessorAnalysis, editable);

        } catch (JsonProcessingException ex) {
            throw new InvalidContentException(GET_ASSESSMENT_ANALYSIS_UNABLE_TO_PARSE_JSON, ex);
        }
    }
}
