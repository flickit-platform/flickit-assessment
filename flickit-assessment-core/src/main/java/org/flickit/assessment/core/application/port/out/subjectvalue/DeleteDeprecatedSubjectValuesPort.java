package org.flickit.assessment.core.application.port.out.subjectvalue;

import java.util.UUID;

public interface DeleteDeprecatedSubjectValuesPort {

    void deleteDeprecatedSubjectValues(UUID assessmentResultId);
}
