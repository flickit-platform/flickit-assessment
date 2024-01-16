package org.flickit.assessment.kit.application.port.out.minio;

public interface LoadKitDSLJsonFilePort {

    String load(String path, String versionId);
}
