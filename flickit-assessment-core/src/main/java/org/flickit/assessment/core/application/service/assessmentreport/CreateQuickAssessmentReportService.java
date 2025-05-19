package org.flickit.assessment.core.application.service.assessmentreport;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.assessmentreport.CreateQuickAssessmentReportUseCase;
import org.flickit.assessment.core.application.port.out.assessmentreport.CreateAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.service.insight.InitAssessmentInsightsHelper;
import org.flickit.assessment.core.application.service.insight.RegenerateExpiredInsightsHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_QUICK_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateQuickAssessmentReportService implements CreateQuickAssessmentReportUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final InitAssessmentInsightsHelper initAssessmentInsightsHelper;
    private final RegenerateExpiredInsightsHelper regenerateExpiredInsightsHelper;
    private final LoadAssessmentReportPort loadAssessmentReportPort;
    private final CreateAssessmentReportPort createAssessmentReportPort;

    @Override
    public void create(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_QUICK_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));

        var locale = Locale.of(assessmentResult.getLanguage().getCode());
        initAssessmentInsightsHelper.initInsights(assessmentResult, locale);
        regenerateExpiredInsightsHelper.regenerateExpiredInsights(assessmentResult, locale);

        if (loadAssessmentReportPort.load(param.getAssessmentId()).isEmpty())
            createAssessmentReportPort.persist(toParam(assessmentResult.getId(), param.getCurrentUserId()));
    }

    private CreateAssessmentReportPort.QuickAssessmentReportParam toParam(UUID assessmentResultId, UUID currentUserId) {
        return new CreateAssessmentReportPort.QuickAssessmentReportParam(assessmentResultId,
            LocalDateTime.now(),
            currentUserId
        );
    }
}
