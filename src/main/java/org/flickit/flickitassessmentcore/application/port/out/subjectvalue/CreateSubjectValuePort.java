package org.flickit.flickitassessmentcore.application.port.out.subjectvalue;

import java.util.List;
import java.util.UUID;

public interface CreateSubjectValuePort {

    void persistAll(List<Long> subjectId, UUID assessmentResultId);
}
