package org.flickit.assessment.kit.application.port.out.kitdsl;

import java.util.Optional;

public interface LoadDslFilePathPort {

    Optional<String> loadDslFilePath(Long kitId);
}
