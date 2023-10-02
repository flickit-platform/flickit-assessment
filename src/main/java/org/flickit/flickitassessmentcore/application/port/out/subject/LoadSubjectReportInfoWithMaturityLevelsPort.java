package org.flickit.flickitassessmentcore.application.port.out.subject;

import org.flickit.flickitassessmentcore.application.domain.AssessmentResult;
import org.flickit.flickitassessmentcore.application.domain.MaturityLevel;

import java.util.List;
import java.util.UUID;

public interface LoadSubjectReportInfoWithMaturityLevelsPort {

    AssessmentResult loadWithMaturityLevels(UUID assessmentId, Long subjectId, List<MaturityLevel> maturityLevels);
}
