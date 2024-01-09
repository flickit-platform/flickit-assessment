package org.flickit.assessment.kit.application.port.out.minio;

import java.io.File;

public interface LoadJsonFilePort {

    String load(String path, String versionId);
}
