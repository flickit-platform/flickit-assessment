package org.flickit.assessment.core.application.port.out.assessmentsubject;

import org.flickit.assessment.core.domain.AssessmentSubject;

import java.util.List;

public interface LoadAssessmentSubjectByAssessmentKitPort {

    List<AssessmentSubject> loadSubjectByKitId(Long kitId);
}
