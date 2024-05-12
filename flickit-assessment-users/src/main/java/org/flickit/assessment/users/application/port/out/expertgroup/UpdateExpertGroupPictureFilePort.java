package org.flickit.assessment.users.application.port.out.expertgroup;

import org.springframework.web.multipart.MultipartFile;

public interface UpdateExpertGroupPictureFilePort {

    String updatePicture(MultipartFile picture, String path);
}
