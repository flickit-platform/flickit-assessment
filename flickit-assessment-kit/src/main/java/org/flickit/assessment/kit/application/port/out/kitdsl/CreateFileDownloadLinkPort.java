package org.flickit.assessment.kit.application.port.out.kitdsl;

import java.time.Duration;

public interface CreateFileDownloadLinkPort {

    String createDownloadLink(String filePath, Duration expiryDuration);
}
