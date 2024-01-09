package org.flickit.assessment.kit.application.port.out.minio;

public interface LoadJsonFilePort {

    String load(String path, String versionId);
}
