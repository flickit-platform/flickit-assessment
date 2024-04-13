package org.flickit.assessment.kit.application.port.out.assessmentkittag;

import org.flickit.assessment.kit.application.domain.KitTag;

import java.util.List;

public interface LoadKitTagPort {

    List<KitTag> load(Long assessmentKitId);
}
