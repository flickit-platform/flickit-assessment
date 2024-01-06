package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.springframework.web.multipart.MultipartFile;

public interface UploadKitPort {

    Result upload(MultipartFile dslZipFile, String dslJsonFile);

    record Result(String zipFileUrl, String zipFileVersionId, String jsonFileUrl,String jsonFileVersionId, String filePath) {}
}
