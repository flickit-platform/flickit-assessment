package org.flickit.assessment.kit.application.port.out.kitdsl;

import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;

import java.util.Map;

public interface ConvertAssessmentKitDslModelPort {

    Map<String, String> toDsl(AssessmentKitDslModel dslModel);
}
