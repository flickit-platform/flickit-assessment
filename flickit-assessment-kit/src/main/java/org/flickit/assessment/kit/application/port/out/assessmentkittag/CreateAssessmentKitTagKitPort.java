package org.flickit.assessment.kit.application.port.out.assessmentkittag;

import java.util.List;

public interface CreateAssessmentKitTagKitPort {

    void persist(List<Long> tagIds, Long kitId);
}
