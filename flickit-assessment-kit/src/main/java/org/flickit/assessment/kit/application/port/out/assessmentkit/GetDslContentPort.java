package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.springframework.web.multipart.MultipartFile;

public interface GetDslContentPort {

    String getDslContent(MultipartFile dslFile);
}
