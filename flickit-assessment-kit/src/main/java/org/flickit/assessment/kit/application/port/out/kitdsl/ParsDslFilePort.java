package org.flickit.assessment.kit.application.port.out.kitdsl;

import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ParsDslFilePort {

    AssessmentKitDslModel parsToDslModel(MultipartFile dslFile) throws IOException;
}
