package org.flickit.assessment.kit.application.port.out.kitversion;

public interface ExistKitVersionByKitIdAndStatusPort {

    boolean exists(long kitId, int status);
}
