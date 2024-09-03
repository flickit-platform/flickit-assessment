package org.flickit.assessment.core.application.port.out.minio;

import java.io.InputStream;

public interface ReadAssessmentAnalysisFilePort {

    InputStream readFileContent(String fileName);
}
