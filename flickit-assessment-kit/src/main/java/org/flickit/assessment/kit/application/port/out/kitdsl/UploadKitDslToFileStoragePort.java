package org.flickit.assessment.kit.application.port.out.kitdsl;

import org.springframework.web.multipart.MultipartFile;

public interface UploadKitDslToFileStoragePort {

    Result uploadKitDsl(MultipartFile dslZipFile, String dslJsonFile);

    record Result(String dslFilePath, String jsonFilePath) {}
}
