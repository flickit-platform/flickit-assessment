package org.flickit.flickitassessmentcore.application.port.out.assessmentsubject;

import java.util.List;

public interface LoadAssessmentSubjectIdsAndQualityAttributeIdsPort {

    ResponseParam loadByAssessmentKitId(Long assessmentKitId);

    record ResponseParam(List<Long> assessmentSubjectIds, List<Long> qualityAttributeIds) {
    }
}
