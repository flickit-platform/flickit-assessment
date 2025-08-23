package org.flickit.assessment.core.application.port.out.minio;

import org.springframework.web.multipart.MultipartFile;

public interface UploadAssessmentAnalysisInputFilePort {

    String uploadAssessmentAnalysisInputFile(MultipartFile inputFile);
}
