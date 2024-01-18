package org.flickit.assessment.kit.application.port.out.expertgroup;

import org.springframework.web.multipart.MultipartFile;

public interface UploadExpertGroupPicturePort {

    String upload(MultipartFile pictureFile);
}
