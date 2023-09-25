package org.flickit.flickitassessmentcore.application.port.out.assessment;

public interface CountAssessmentsByKitPort {

    int count(Long assessmentKitId, Boolean includeDeleted, Boolean includeNotDeleted);

}
