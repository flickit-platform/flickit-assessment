package org.flickit.assessment.core.application.port.out.assessmentuserrole;

import org.flickit.assessment.core.application.domain.FullUser;

import java.util.List;
import java.util.UUID;

public interface LoadGraphicalReportUsersPort {

    List<FullUser> load(UUID assessmentId, List<Integer> roleIds);
}
