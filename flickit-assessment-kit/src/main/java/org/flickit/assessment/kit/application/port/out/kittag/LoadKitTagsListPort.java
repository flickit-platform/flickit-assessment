package org.flickit.assessment.kit.application.port.out.kittag;

import org.flickit.assessment.kit.application.domain.KitTag;

import java.util.List;

public interface LoadKitTagsListPort {

    List<KitTag> load(long kitId);
}
