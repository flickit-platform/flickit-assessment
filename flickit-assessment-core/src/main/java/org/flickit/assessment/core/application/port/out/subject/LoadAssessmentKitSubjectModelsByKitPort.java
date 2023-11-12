package org.flickit.assessment.core.application.port.out.subject;

import org.flickit.assessment.kit.domain.Subject;

import java.util.List;

public interface LoadAssessmentKitSubjectModelsByKitPort {

    List<Subject> load(Long assessmentKitId);
}
