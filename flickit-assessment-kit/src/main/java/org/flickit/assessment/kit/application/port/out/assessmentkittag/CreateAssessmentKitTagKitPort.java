package org.flickit.assessment.kit.application.port.out.assessmentkittag;

public interface CreateAssessmentKitTagKitPort {

    Long persist(Param param);

    record Param(Long tagId, Long kitId) {}
}
