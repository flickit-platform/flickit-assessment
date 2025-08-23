package org.flickit.assessment.users.application.port.out.user;

import org.springframework.web.multipart.MultipartFile;

public interface UploadUserProfilePicturePort {

    String uploadUserProfilePicture(MultipartFile pictureFile);
}
