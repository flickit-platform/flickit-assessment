package org.flickit.assessment.core.application.port.out.assessmentinvite;

import java.util.List;
import java.util.UUID;

public interface LoadGraphicalReportInviteesPort {

    List<Result> load(UUID assessmentId, List<Integer> roleIds);

    record Result(String email) {}
}
