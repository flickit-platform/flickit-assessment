package org.flickit.assessment.kit.application.port.out.kitdsl;

import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.springframework.web.multipart.MultipartFile;

public interface ConvertExcelToDslModelPort {

    AssessmentKitDslModel convert(MultipartFile excelFile);
}
