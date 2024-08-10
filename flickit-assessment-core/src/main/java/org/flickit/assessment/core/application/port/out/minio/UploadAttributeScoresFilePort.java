package org.flickit.assessment.core.application.port.out.minio;

import java.io.InputStream;

public interface UploadAttributeScoresFilePort {

    String uploadExcel(InputStream content, String fileName);
}
