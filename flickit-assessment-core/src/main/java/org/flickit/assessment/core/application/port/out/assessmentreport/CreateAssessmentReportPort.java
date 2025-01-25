package org.flickit.assessment.core.application.port.out.assessmentreport;

import org.flickit.assessment.core.application.domain.AssessmentReport;

public interface CreateAssessmentReportPort {

    void persist(AssessmentReport assessmentReport);
}
