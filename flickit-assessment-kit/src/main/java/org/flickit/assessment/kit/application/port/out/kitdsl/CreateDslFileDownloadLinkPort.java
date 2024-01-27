package org.flickit.assessment.kit.application.port.out.kitdsl;

import java.util.Optional;

public interface CreateDslFileDownloadLinkPort {

    Optional<String> loadDslFilePath(Long kitId);
}
