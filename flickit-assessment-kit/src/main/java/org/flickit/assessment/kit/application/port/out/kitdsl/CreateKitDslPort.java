package org.flickit.assessment.kit.application.port.out.kitdsl;

import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface CreateKitDslPort {

    Long create(String dslFilePath, String jsonFilePath, UUID createdBy);

    AssessmentKitDslModel convert(MultipartFile excelFile);
}
