package org.flickit.assessment.kit.application.port.out.expertgroup;

import java.time.Duration;

public interface LoadExpertGroupMembersPictureLinkPort {

    String loadMembersPictureLink(String filePath, Duration expiryDuration);
}
