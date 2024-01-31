package org.flickit.assessment.kit.application.port.out.expertgroup;

import java.time.Duration;

public interface LoadExpertGroupPictureLinkPort {

    String loadPictureLink(String filePath, Duration expiryDuration);
}
