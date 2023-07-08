package org.flickit.flickitassessmentcore.application.port.out.subject;

import java.util.List;

public interface LoadSubjectIdsAndQualityAttributeIdsPort {

    ResponseParam loadByAssessmentKitId(Long assessmentKitId);

    record ResponseParam(List<Long> subjectIds, List<Long> qualityAttributeIds) {
    }
}
