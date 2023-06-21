package org.flickit.flickitassessmentcore.application.port.out.assessmentsubject;

import java.util.List;

public interface LoadAssessmentSubjectIdsByAssessmentKitPort {

    List<Long> loadIdsByAssessmentKitId(Long assessmentKitId);
}
