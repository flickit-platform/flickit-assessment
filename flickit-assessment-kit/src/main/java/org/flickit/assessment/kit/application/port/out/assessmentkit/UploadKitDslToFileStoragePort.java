package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.springframework.web.multipart.MultipartFile;

public interface UploadKitDslToFileStoragePort {

    Result upload(MultipartFile dslZipFile, String dslJsonFile);

    record Result(String zipFilePath, String jsonFilePath) {}
}
