package org.flickit.assessment.core.application.port.out.assessmentkit;

import java.util.UUID;

public interface LoadKitInfoPort {

    Result loadKitInfo(long id);

    record Result(String title, UUID createdBy, long expertGroupId) {}
}
