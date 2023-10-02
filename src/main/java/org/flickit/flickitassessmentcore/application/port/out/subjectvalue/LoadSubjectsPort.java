package org.flickit.flickitassessmentcore.application.port.out.subjectvalue;

import java.util.List;
import java.util.UUID;

public interface LoadSubjectsPort {

    List<Long> loadSubjectIdsByAssessmentId(UUID assessmentId);
}
