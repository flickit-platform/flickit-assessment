package org.flickit.assessment.kit.application.port.out.assessmentkitdsl;

public interface UpdateAssessmentKitDslPort {

    void update(Param param);

    record Param(Long id, Long kitId) {}
}
