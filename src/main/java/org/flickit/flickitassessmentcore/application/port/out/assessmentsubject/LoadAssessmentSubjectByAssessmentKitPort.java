package org.flickit.flickitassessmentcore.application.port.out.assessmentsubject;

import org.flickit.flickitassessmentcore.domain.AssessmentSubject;

import java.util.List;

public interface LoadAssessmentSubjectByAssessmentKitPort {

    List<AssessmentSubject> loadSubjectByKitId(Long kitId);
}
